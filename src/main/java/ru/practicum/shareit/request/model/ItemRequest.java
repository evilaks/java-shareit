package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private LocalDateTime created;

    @OneToMany(mappedBy = "itemRequest", fetch = FetchType.EAGER)
    private List<Item> items;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

}
