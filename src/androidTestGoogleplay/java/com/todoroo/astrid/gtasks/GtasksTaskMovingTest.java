/**
 * Copyright (c) 2012 Todoroo Inc
 *
 * See the file "LICENSE" for the full license governing this code.
 */
package com.todoroo.astrid.gtasks;

import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.todoroo.astrid.dao.MetadataDao;
import com.todoroo.astrid.data.Metadata;
import com.todoroo.astrid.data.Task;
import com.todoroo.astrid.service.TaskService;
import com.todoroo.astrid.test.DatabaseTestCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("nls")
public class GtasksTaskMovingTest extends DatabaseTestCase {

    private static final int VALUE_UNSET = -1;

    @Inject GtasksListService gtasksListService;
    @Inject GtasksMetadataService gtasksMetadataService;
    @Inject GtasksTaskListUpdater gtasksTaskListUpdater;
    @Inject MetadataDao metadataDao;
    @Inject TaskService taskService;
    @Inject GtasksMetadata gtasksMetadata;

    private Task A, B, C, D, E, F;
    private GtasksList list;

    /* Starting State:
     *
     * A
     *  B
     *  C
     *   D
     * E
     * F
     */

    public void disabled_testMoveDownFromListBottom() {
        givenTasksABCDEF();

        whenTriggerMove(F, null);

        thenExpectMetadataOrderAndIndent(E, 4, 0);
        thenExpectMetadataOrderAndIndent(F, 5, 0);
    }

    public void disabled_testMoveDownToListBottom() {
        givenTasksABCDEF();

        whenTriggerMove(E, null);

        thenExpectMetadataOrderAndIndent(E, 5, 0);
        thenExpectMetadataOrderAndIndent(F, 4, 0);
    }

    public void disabled_testMoveUpSimple() {
        givenTasksABCDEF();

        whenTriggerMove(F, E);

        thenExpectMetadataOrderAndIndent(E, 5, 0);
        thenExpectMetadataOrderAndIndent(F, 4, 0);
    }

    public void disabled_testMoveUpWithSubtasks() {
        givenTasksABCDEF();

        whenTriggerMove(C, B);

        /*
         * A
         *  C
         *   D
         *  B
         */

        thenExpectMetadataOrderAndIndent(A, 0, 0);
        thenExpectMetadataOrderAndIndent(B, 3, 1);
        thenExpectMetadataOrderAndIndent(C, 1, 1);
        thenExpectMetadataOrderAndIndent(D, 2, 2);
    }

    public void disabled_testMoveDownThroughSubtasks() {
        givenTasksABCDEF();

        whenTriggerMove(B, E);

        /*
         * A
         *  C
         *   D
         * B
         * E
         */

        thenExpectMetadataOrderAndIndent(A, 0, 0);
        thenExpectMetadataOrderAndIndent(B, 3, 0);
        thenExpectMetadataOrderAndIndent(C, 1, 1);
        thenExpectMetadataOrderAndIndent(D, 2, 2);
    }

    public void disabled_testMoveUpAboveParent() {
        givenTasksABCDEF();

        whenTriggerMove(B, A);

        /*
         * B
         * A
         *  C
         *   D
         * E
         * F
         */

        thenExpectMetadataOrderAndIndent(A, 1, 0);
        thenExpectMetadataOrderAndIndent(B, 0, 0);
        thenExpectMetadataOrderAndIndent(C, 2, 1);
    }

    public void disabled_testMoveDownWithChildren() {
        givenTasksABCDEF();

        whenTriggerMove(C, F);

        /*
         * A
         *  B
         * E
         * C
         *  D
         * F
         */

        thenExpectMetadataOrderAndIndent(A, 0, 0);
        thenExpectMetadataOrderAndIndent(B, 1, 1);
        thenExpectMetadataOrderAndIndent(C, 3, 0);
        thenExpectMetadataOrderAndIndent(D, 4, 1);
        thenExpectMetadataOrderAndIndent(E, 2, 0);
    }

    public void disabled_testMoveDownIndentingTwice() {
        givenTasksABCDEF();

        whenTriggerMove(D, F);

        /*
         * A
         *  B
         *  C
         * E
         * D
         */

        thenExpectMetadataOrderAndIndent(A, 0, 0);
        thenExpectMetadataOrderAndIndent(B, 1, 1);
        thenExpectMetadataOrderAndIndent(C, 2, 1);
        thenExpectMetadataOrderAndIndent(D, 4, 0);
        thenExpectMetadataOrderAndIndent(E, 3, 0);
    }

    public void disabled_testMoveUpMultiple() {
        givenTasksABCDEF();

        whenTriggerMove(C, A);

        /*
         * C
         *  D
         * A
         *  B
         */

        thenExpectMetadataOrderAndIndent(A, 2, 0);
        thenExpectMetadataOrderAndIndent(B, 3, 1);
        thenExpectMetadataOrderAndIndent(C, 0, 0);
        thenExpectMetadataOrderAndIndent(D, 1, 1);
    }

    public void disabled_testMoveUpIntoSublist() {
        givenTasksABCDEF();

        whenTriggerMove(F, D);

        /*
         * A
         *  B
         *  C
         *   F
         *   D
         */

        thenExpectMetadataOrderAndIndent(A, 0, 0);
        thenExpectMetadataOrderAndIndent(B, 1, 1);
        thenExpectMetadataOrderAndIndent(C, 2, 1);
        thenExpectMetadataOrderAndIndent(D, 4, 2);
        thenExpectMetadataOrderAndIndent(E, 5, 0);
        thenExpectMetadataOrderAndIndent(F, 3, 2);
    }

    public void disabled_testMoveDownMultiple() {
        givenTasksABCDEF();

        whenTriggerMove(B, F);

        /*
         * A
         *  C
         *   D
         * E
         * B
         */

        thenExpectMetadataOrderAndIndent(A, 0, 0);
        thenExpectMetadataOrderAndIndent(B, 4, 0);
        thenExpectMetadataOrderAndIndent(C, 1, 1);
        thenExpectMetadataOrderAndIndent(D, 2, 2);
        thenExpectMetadataOrderAndIndent(E, 3, 0);
        thenExpectMetadataOrderAndIndent(F, 5, 0);
    }


    // --- helpers

    /** moveTo = null => move to end */
    private void whenTriggerMove(Task target, Task moveTo) {
        gtasksTaskListUpdater.moveTo(list, target.getId(), moveTo == null ? -1 : moveTo.getId());
    }

    private void thenExpectMetadataOrderAndIndent(Task task, long order, int indent) {
        Metadata metadata = gtasksMetadataService.getTaskMetadata(task.getId());
        assertNotNull("metadata was found", metadata);
        assertEquals("order", order, metadata.getValue(GtasksMetadata.ORDER).longValue());
        assertEquals("indentation", indent, (int)metadata.getValue(GtasksMetadata.INDENT));
    }

    @Override
    protected void setUp() {
        super.setUp();

        TaskLists lists = new TaskLists();
        List<TaskList> items = new ArrayList<>();
        TaskList taskList = new TaskList();
        taskList.setId("1");
        taskList.setTitle("Tim's Tasks");
        items.add(taskList);
        lists.setItems(items);
        gtasksListService.updateLists(lists);

        list = gtasksListService.getLists().get(0);
    }

    /**
     * A
     *  B
     *  C
     *   D
     * E
     * F
     */
    private void givenTasksABCDEF() {
        A = createTask("A", 0, 0);
        B = createTask("B", 1, 1);
        C = createTask("C", 2, 1);
        D = createTask("D", 3, 2);
        E = createTask("E", 4, 0);
        F = createTask("F", 5, 0);
    }

    private Task createTask(String title, long order, int indent) {
        Task task = new Task();
        task.setTitle(title);
        taskService.save(task);
        Metadata metadata = gtasksMetadata.createEmptyMetadata(task.getId());
        metadata.setValue(GtasksMetadata.LIST_ID, "1");
        if(order != VALUE_UNSET)
            metadata.setValue(GtasksMetadata.ORDER, order);
        if(indent != VALUE_UNSET)
            metadata.setValue(GtasksMetadata.INDENT, indent);
        metadataDao.persist(metadata);
        return task;
    }

}
