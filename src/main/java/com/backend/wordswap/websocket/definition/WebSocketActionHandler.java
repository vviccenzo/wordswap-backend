package com.backend.wordswap.websocket.definition;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.backend.wordswap.conversation.ConversationService;
import com.backend.wordswap.conversation.dto.ConversationGroupCreateDTO;
import com.backend.wordswap.friendshipRequest.FriendshipRequestService;
import com.backend.wordswap.friendshipRequest.dto.FriendshipDeleteRequestDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipRequestCreateDTO;
import com.backend.wordswap.friendshipRequest.dto.FriendshipRequestUpdateDTO;
import com.backend.wordswap.message.MessageService;
import com.backend.wordswap.message.dto.MessageCreateDTO;
import com.backend.wordswap.message.dto.MessageDeleteDTO;
import com.backend.wordswap.message.dto.MessageEditDTO;
import com.backend.wordswap.message.dto.MessageViewDTO;

public enum WebSocketActionHandler {

    SEND_MESSAGE(WebSocketAction.SEND_MESSAGE, "sendMessage", MessageService.class, MessageCreateDTO.class),
    EDIT_MESSAGE(WebSocketAction.EDIT_MESSAGE, "editMessage", MessageService.class, MessageEditDTO.class),
    DELETE_MESSAGE(WebSocketAction.DELETE_MESSAGE, "deleteMessage", MessageService.class, MessageDeleteDTO.class),
    SEND_FRIEND_REQUEST(WebSocketAction.SEND_FRIEND_REQUEST, "sendInvite", FriendshipRequestService.class, FriendshipRequestCreateDTO.class),
    DELETE_FRIEND(WebSocketAction.DELETE_FRIEND, "deleteFriendship", FriendshipRequestService.class, FriendshipDeleteRequestDTO.class),
    UPDATE_FRIEND_REQUEST(WebSocketAction.UPDATE_FRIEND_REQUEST, "changeStatus", FriendshipRequestService.class, FriendshipRequestUpdateDTO.class),
    CREATE_GROUP(WebSocketAction.CREATE_GROUP, "createGroup", ConversationService.class, ConversationGroupCreateDTO.class),
    VIEW_MESSAGE(WebSocketAction.VIEW_MESSAGE, "viewMessages", MessageService.class, MessageViewDTO.class),
    USER_TYPING(WebSocketAction.USER_TYPING, "typingMessage", MessageService.class, MessageTypingDTO.class);

    private static ApplicationContext applicationContext;
    private final WebSocketAction action;
    private final String methodName;
    private final Class<?> serviceClass;
    private final Class<?> dtoClass;

    private static final Map<WebSocketAction, WebSocketActionHandler> actionHandlerMap = new HashMap<>();

    static {
        for (WebSocketActionHandler handler : WebSocketActionHandler.values()) {
            actionHandlerMap.put(handler.action, handler);
        }
    }

    WebSocketActionHandler(WebSocketAction action, String methodName, Class<?> serviceClass, Class<?> dtoClass) {
        this.action = action;
        this.methodName = methodName;
        this.serviceClass = serviceClass;
        this.dtoClass = dtoClass;
    }

    public WebSocketAction getAction() {
        return action;
    }

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public void execute(WebSocketRequest request) throws Exception {
        if (!dtoClass.isInstance(request.getDtoBasedOnAction())) {
            throw new IllegalArgumentException("Tipo de request inválido para a ação: " + action);
        }

        Object service = applicationContext.getBean(serviceClass);
        Method method = service.getClass().getMethod(methodName, dtoClass);

        method.invoke(service, request.getDtoBasedOnAction());
    }

    public static WebSocketActionHandler getHandler(WebSocketAction action) {
        WebSocketActionHandler handler = actionHandlerMap.get(action);
        if (handler == null) {
            throw new IllegalArgumentException("Ação desconhecida: " + action);
        }
        return handler;
    }
}
