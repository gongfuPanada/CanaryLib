package net.canarymod.hook;

import com.google.common.collect.ArrayListMultimap;
import net.canarymod.ToolBox;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.plugin.RegisteredPluginListener;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import static net.canarymod.Canary.log;

/**
 * Stores registered listeners and performs hook dispatches.
 *
 * @author Chris (damagefilter)
 * @author Jos Kuijpers
 * @author Yariv Livay
 */
public class HookExecutor implements HookExecutorInterface {
    private final PluginComparator listener_comp = new PluginComparator();
    final ArrayListMultimap<Class<? extends Hook>, RegisteredPluginListener> listeners = ArrayListMultimap.create();

    /** Register a {@link PluginListener} for a system hook */
    @Override
    public void registerListener(PluginListener listener, Plugin plugin) {
        Method[] methods = ToolBox.safeArrayMerge(listener.getClass().getMethods(), listener.getClass().getDeclaredMethods(), new Method[1]);
        for (final Method method : methods) {
            // Check if the method is a hook handling method
            final HookHandler handler = method.getAnnotation(HookHandler.class);

            if (handler == null) {
                continue; // Next, not one of our things
            }
            // Check the parameters for number and type and decide if it's one
            // that is really a handler method
            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length > 1 || parameters.length == 0) {
                throw new HookConsistencyException("Amount of parameters for " + method.getName() + " is invalid. Expected 1, was " + parameters.length);
            }
            Class<?> hookCls = parameters[0];

            if (!Hook.class.isAssignableFrom(hookCls)) {
                throw new HookConsistencyException("Hook is not assignable from " + hookCls.getName());
            }

            Dispatcher dispatcher = new Dispatcher() {

                @Override
                public void execute(PluginListener listener, Hook hook) {
                    try {
                        method.invoke(listener, hook);
                    }
                    catch (Exception ex) {
                        throw new HookExecutionException(ex.getMessage(), ex);
                    }
                }
            };
            dispatcher.ignoreCanceled = handler.ignoreCanceled();

            // We checked the class above, ignore unchecked warnings.
            listeners.put((Class<? extends Hook>) hookCls, new RegisteredPluginListener(listener, plugin, dispatcher, handler.priority()));
            Collections.sort(listeners.get((Class<? extends Hook>) hookCls), listener_comp);
        }
    }

    /**
     * Unregisters all listeners for specified plugin
     *
     * @param plugin
     *         the {@link Plugin} instance
     */
    @Override
    public void unregisterPluginListeners(Plugin plugin) {
        Iterator<RegisteredPluginListener> iter = listeners.values().iterator();
        while (iter.hasNext()) {
            RegisteredPluginListener listener = iter.next();
            if (listener.getPlugin().equals(plugin)) {
                iter.remove();
            }
        }
    }

    /** Call a system hook */
    @Override
    public void callHook(Hook hook) {
        if (hook.executed()) {
            return;
        }
        hook.hasExecuted();
        if (!this.listeners.containsKey(hook.getClass())) {
            return;
        }
        Iterator<RegisteredPluginListener> iter = this.listeners.get(hook.getClass()).iterator();
        while (iter.hasNext()) {
            RegisteredPluginListener listener = iter.next();
            try {
                listener.execute(hook);
            }
            catch (HookExecutionException hexex) {
                log.error("Exception while executing Hook: " + hook.getHookName() + " in PluginListener: " +
                        listener.getListener().getClass().getSimpleName() + " (Plugin: " + listener.getPlugin().getName() + ")", hexex.getCause());
            }
        }
    }

    class PluginComparator implements Comparator<RegisteredPluginListener> {
        @Override
        public int compare(RegisteredPluginListener o1, RegisteredPluginListener o2) {
            if (o1 == o2) {
                return 0;
            }
            int diff = o2.getMethodPriority().getPriorityValue() - o1.getMethodPriority().getPriorityValue();

            if (diff == 0) {
                diff = o2.getPluginPriority() - o1.getPluginPriority();
            }
            return (int) Math.signum(diff);
        }
    }
}
