package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemStorageTests {

    private ItemStorage itemStorage;

    private Item testItem1;
    private Item testItem2;


    @BeforeEach
    public void beforeEach() {
        itemStorage = new InMemoryItemStorage();
        testInit();
    }

    @Test
    public void addTest() {
        Item actual = itemStorage.add(testItem1);

        assertThat(actual).isNotNull();
        assertThat(actual).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void findTest() {
        itemStorage.add(testItem1);

        Item actual = itemStorage.findById(1L);

        assertEquals(testItem1.getName(), actual.getName());
        assertEquals(testItem1.getDescription(), actual.getDescription());
    }

    @Test
    public void findAllTest() {
        itemStorage.add(testItem1);
        itemStorage.add(testItem2);

        List<Item> actual = itemStorage.findAllByOwnerId(1L);

        assertThat(actual).isNotEmpty();
        assertThat(actual).size().isEqualTo(1);

    }

    @Test
    public void updateTest() {
        itemStorage.add(testItem1);

        testItem1.setName("updated name");
        testItem1.setDescription("updated description");

        itemStorage.update(1L, testItem1);

        Item actual = itemStorage.findById(1L);

        assertEquals(actual, testItem1);
    }

    @Test
    public void deleteTest() {
        itemStorage.add(testItem1);

        itemStorage.delete(1L);

        assertThat(itemStorage.findById(1L)).isNull();
    }

    private void testInit() {
        User testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setName("username");
        testUser1.setEmail("user@example.com");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("second_username");
        testUser2.setEmail("seconduser@example.com");

        testItem1 = new Item();
        testItem1.setName("itemname");
        testItem1.setDescription("itemdesc");
        testItem1.setIsAvailable(true);
        testItem1.setOwner(testUser1);

        testItem2 = new Item();
        testItem2.setName("seconditemname");
        testItem2.setDescription("seconditemdesc");
        testItem2.setIsAvailable(true);
        testItem2.setOwner(testUser2);
    }

}
