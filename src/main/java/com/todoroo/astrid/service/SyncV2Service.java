/**
 * Copyright (c) 2012 Todoroo Inc
 *
 * See the file "LICENSE" for the full license governing this code.
 */
package com.todoroo.astrid.service;

import com.todoroo.astrid.gtasks.GtasksList;
import com.todoroo.astrid.gtasks.sync.GtasksSyncV2Provider;
import com.todoroo.astrid.sync.SyncResultCallback;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * SyncV2Service is a simplified synchronization interface for supporting
 * next-generation sync interfaces such as Google Tasks and Astrid.com
 *
 * @author Tim Su <tim@astrid.com>
 *
 */
@Singleton
public class SyncV2Service {

    /*
     * At present, sync provider interactions are handled through code. If
     * there is enough interest, the Astrid team could create an interface
     * for responding to sync requests through this new API.
     */
    private final GtasksSyncV2Provider provider;

    @Inject
    public SyncV2Service(GtasksSyncV2Provider gtasksSyncV2Provider) {
        provider = gtasksSyncV2Provider;
    }

    public boolean isActive() {
        return provider.isActive();
    }

    /**
     * Initiate synchronization of active tasks
     *
     * @param callback result callback
     * @return true if any servide was logged in and initiated a sync
     */
    public boolean synchronizeActiveTasks(SyncResultCallback callback) {
        if (provider.isActive()) {
            provider.synchronizeActiveTasks(callback);
            return true;
        }
        return false;
    }

    /**
     * Initiate synchronization of task list
     *
     * @param list list object
     * @param callback result callback
     */
    public void synchronizeList(GtasksList list, SyncResultCallback callback) {
        if(provider.isActive()) {
            provider.synchronizeList(list, callback);
        }
    }

    public void clearCompleted(GtasksList list, SyncResultCallback callback) {
        if (provider.isActive()) {
            provider.clearCompleted(list, callback);
        }
    }
}
