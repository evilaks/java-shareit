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
        User testUser1 = User.builder()
                .id(1L)
                .name("username")
                .email("user@example.com")
                .build();

        User testUser2 = User.builder()
                .id(2L)
                .name("second_username")
                .email("seconduser@example.com")
                .build();

        testItem1 = Item.builder()
                .name("itemname")
                .description("itemdesc")
                .isAvailable(true)
                .owner(testUser1)
                .build();

        testItem2 = Item.builder()
                .name("seconditemname")
                .description("seconditemdesc")
                .isAvailable(true)
                .owner(testUser2)
                .build();
    }
}
