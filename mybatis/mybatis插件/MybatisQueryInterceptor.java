package com.pbs.suite.senior.interceptor;

import com.pbs.suite.senior.thread.SeniorThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

/**
 *针对特定的业务
 *需要对sql进行一些条件添加
 *在不影响现有的业务，才用mybatis拦截器进行插入
 */
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Slf4j
public class SeniorInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        //针对特定的业务进行拦截测试
        if (ms.getId()!=null && !ms.getId().contains("getData")){
            return invocation.proceed();
        }
        String sqlStr = SeniorThreadLocal.get();
        if (StringUtils.isEmpty(sqlStr)){
            return invocation.proceed();
        }
        BoundSql boundSql;
        if(args.length == 4){

            boundSql = ms.getBoundSql(parameter);
        } else {
            boundSql = (BoundSql) args[5];
        }
        Executor executor = (Executor) invocation.getTarget();
        //获取sql语句
        String sql = boundSql.getSql();
        //进行sql条件注入
        if (sql.lastIndexOf("1=1")==-1){
            return invocation.proceed();
        }
        StringBuilder sb=new StringBuilder(sql);
        StringBuilder replace = sb.replace(sql.lastIndexOf("1=1"), sql.lastIndexOf("1=1") + 3, sqlStr);
        sql=replace.toString();
        BoundSql newBoundSql=new BoundSql(ms.getConfiguration(), sql,boundSql.getParameterMappings(),boundSql.getParameterObject());
        //可以对参数做各种处理
        CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, newBoundSql);
        return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, newBoundSql);
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
