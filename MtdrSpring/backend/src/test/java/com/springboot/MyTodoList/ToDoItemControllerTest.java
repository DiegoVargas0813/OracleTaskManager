//test with mockito for ToDoItemController
package com.springboot.MyTodoList;
import com.springboot.MyTodoList.controller.ToDoItemController;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(ToDoItemController.class)
public class ToDoItemControllerTest {
    @Mock
    private ToDoItemService toDoItemService;

    @InjectMocks
    private ToDoItemController toDoItemController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    OffsetDateTime creation_ts = OffsetDateTime.now();

    @Test
    public void testGetAllToDoItems() {
        List<ToDoItem> mockToDoItems = new ArrayList<>();
        mockToDoItems.add(new ToDoItem(1, "Test Item 1",creation_ts, false));
        mockToDoItems.add(new ToDoItem(2, "Test Item 2",creation_ts, true));

        when(toDoItemService.findAll()).thenReturn(mockToDoItems);

        List<ToDoItem> result = toDoItemController.getAllToDoItems();

        assertEquals(2, result.size());
        verify(toDoItemService, times(1)).findAll();
    }
    @Test
    public void testGetToDoItemById() {
        ToDoItem mockToDoItem = new ToDoItem(1, "Test Item",creation_ts, false);

        when(toDoItemService.getItemById(1)).thenReturn(new ResponseEntity<>(mockToDoItem, HttpStatus.OK));

        ResponseEntity<ToDoItem> result = toDoItemController.getToDoItemById(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockToDoItem, result.getBody());
        verify(toDoItemService, times(1)).getItemById(1);
    }
    @Test
    public void testAddToDoItem() throws Exception {
        ToDoItem mockToDoItem = new ToDoItem(1, "Test Item",creation_ts, false);

        when(toDoItemService.addToDoItem(any(ToDoItem.class))).thenReturn(mockToDoItem);

        ResponseEntity result = toDoItemController.addToDoItem(mockToDoItem);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(toDoItemService, times(1)).addToDoItem(any(ToDoItem.class));
    }
    @Test
    public void testUpdateToDoItem() {
        ToDoItem mockToDoItem = new ToDoItem(1, "Updated Item",creation_ts, true);

        when(toDoItemService.updateToDoItem(anyInt(), any(ToDoItem.class))).thenReturn(mockToDoItem);

        ResponseEntity result = toDoItemController.updateToDoItem(mockToDoItem, 1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockToDoItem, result.getBody());
        verify(toDoItemService, times(1)).updateToDoItem(anyInt(), any(ToDoItem.class));
    }
    @Test
    public void testDeleteToDoItem() {
        when(toDoItemService.deleteToDoItem(anyInt())).thenReturn(true);

        ResponseEntity<Boolean> result = toDoItemController.deleteToDoItem(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(true, result.getBody());
        verify(toDoItemService, times(1)).deleteToDoItem(anyInt());
    }
}