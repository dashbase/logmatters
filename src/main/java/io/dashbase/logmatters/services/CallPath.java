package io.dashbase.logmatters.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CallPath {
    private static final Logger logger = LoggerFactory.getLogger(CallPath.class);

    public boolean concurrent = false;
    public List<Map<String, CallPath>> services = new LinkedList<>();


    private static void doCall(String ctx, String name, CallPath callpath, ExecutorService executorService) {
        Service s = ServiceRegistry.INSTANCE.find(name);
        if (s == null) {
            logger.error("ctx: [" + ctx +"] service: " + name + " not found");
        } else {
            try {
                s.execute(ctx, callpath, executorService);
            } catch (Exception ex) {
                logger.error("ctx: [" + ctx + "] service: " + name + " failed", ex);
            }
        }
    }

    public void execute(final String ctx, ExecutorService executorService) {
        if (!services.isEmpty()) {
            if (concurrent) {
                List<Future<?>> futureList = new LinkedList<>();
                services.stream().forEach(
                        map -> {
                            map.entrySet().stream().forEach(
                                    e -> {
                                        futureList.add(executorService.submit(new Runnable() {
                                            @Override
                                            public void run() {
                                                doCall(ctx, e.getKey(), e.getValue(), executorService);
                                            }
                                        }));
                                    }
                            );
                        }
                );
                futureList.forEach(
                        f -> {
                            try {
                                f.get();
                            } catch (Exception e) {
                                logger.error("ctx: [" + ctx + "] async executing service didn't succeed: ", e);
                            }
                        }
                );

            } else {
                // execute sequentially
                services.stream().forEach(
                        map -> {
                            map.entrySet().stream().forEach(
                                    e -> {
                                        doCall(ctx, e.getKey(), e.getValue(), executorService);
                                    }
                            );
                        }
                );
            }
        }
    }
}
