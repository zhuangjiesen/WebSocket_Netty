package com.jason.core.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/27
 */
public class MySessionDAO extends AbstractSessionDAO {
    private static final Logger log= LoggerFactory.getLogger(MySessionDAO.class);

    private static Map<String , Session> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void delete(Session session) {
        // TODO Auto-generated method stub
        sessionMap.remove(session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        // TODO Auto-generated method stub
        return sessionMap.values();
    }


    @Override
    public void update(Session session) throws UnknownSessionException {
        // TODO Auto-generated method stub
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            sessionMap.remove(session.getId());
            return; //如果会话过期/停止 没必要再更新了
        }
        sessionMap.put(session.getId().toString() ,session);
    }

    @Override
    protected Serializable doCreate(Session session) {
        // TODO Auto-generated method stub
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        sessionMap.put(sessionId.toString() , session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        // TODO Auto-generated method stub
        return sessionMap.get(sessionId.toString());
    }

}