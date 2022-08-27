package me.earth.earthhack.api.event.bus.api;

/**
 * The EventBus Api.
 */
@SuppressWarnings("unused")
public interface EventBus
{
    /**
     * Default Listener Priority
     */
    int DEFAULT_PRIORITY = 10;

    /**
     * Posts an object on the bus. Invokes
     * {@link Invoker#invoke(Object)} for every
     * Listener that targets the objects class.
     *
     * @param object the object posted.
     */
    void post(Object object);

    /**
     * Posts an object on the bus. Invokes
     * {@link Invoker#invoke(Object)} for every
     * Listener that targets the objects class and where
     * {@link Listener#getType()} == type.
     *
     * @param object the object posted.
     * @param type the type.
     */
    void post(Object object, Class<?> type);

    /**
     * Posts a cancellable object on the bus. Posting
     * will stop as soon as {@link ICancellable#isCancelled()}
     * returns <tt>true</tt> for the given object.
     *
     * @param object the cancellable object to post.
     * @return {@link ICancellable#isCancelled()} for the given object.
     */
    boolean postCancellable(ICancellable object);

    /**
     * Posts a cancellable object on the bus. Posting
     * will stop as soon as {@link ICancellable#isCancelled()}
     * returns <tt>true</tt> for the given object.
     *
     * @param object the cancellable object to post.
     * @param type the type of the object.
     * @return {@link ICancellable#isCancelled()} for the given object.
     */
    boolean postCancellable(ICancellable object, Class<?> type);

    /**
     * Posts an object on the bus. Invokes
     * {@link Invoker#invoke(Object)} for every
     * Listener that targets the objects class.
     * <p>
     * When normally Listeners with the highest
     * priority would be called first, here the
     * opposite is the case.
     * <p>
     * (I was too lazy to split MotionUpdateEvent
     *  in a post and pre event so I just post it
     *  with this...)
     *
     * @param object the object posted.
     */
    void postReversed(Object object, Class<?> type);

    /**
     * Calls {@code  register} for
     * every listener from {@link Subscriber#getListeners()},
     * if the object implements the Subscriber interface.
     *
     * @param object the subscriber whose listeners get registered.
     */
    void subscribe(Object object);

    /**
     * Calls {@code  unregister} for
     * every listener from {@link Subscriber#getListeners()},
     * if the object implements the Subscriber interface.
     *
     * @param object the subscriber whose listeners get unregistered.
     */
    void unsubscribe(Object object);

    /**
     * Registers a listener to the bus. It will now receive
     * Objects posted on the bus.
     *
     * @param listener the listener to be registered.
     */
    void register(Listener<?> listener);

    /**
     * Unregisters a listener from the bus. Now it
     * wont receive Objects posted on the bus.
     *
     * @param listener the listener to be unregistered.
     */
    void unregister(Listener<?> listener);

    /**
     * Returns {@code true} if the object is a subscriber
     * whose listeners are currently registered or a listener
     * which is currently registered.
     *
     * @return {@code true} if the object is subscribed to the bus.
     */
    boolean isSubscribed(Object object);

    /**
     * Checks if Listeners for Objects of the given class exist.
     * <p>
     * Returns <tt>true</tt> if a Listeners exist for
     * which {@link Listener#getTarget()}
     * equals the given class.
     * <p>
     * Only checks listeners with {@link Listener#getType()} == null.
     *
     * @param clazz the class to check.
     * @return <tt>true</tt> if a Listener for the class exists.
     */
    boolean hasSubscribers(Class<?> clazz);

    /**
     * Checks if Listeners for the given Type of Object exist.
     * <p>
     * Returns <tt>true</tt> if a Listeners exist for
     * which {@link Listener#getTarget()}
     * equals the given class.
     * <p>
     * Checks listeners with
     * {@link Listener#getType()} == null or == the given type.
     *
     * @param clazz the class to check.
     * @param type the type of the given class.
     * @return <tt>true</tt> if a Listener for the class and type exists.
     */
    boolean hasSubscribers(Class<?> clazz, Class<?> type);

}
