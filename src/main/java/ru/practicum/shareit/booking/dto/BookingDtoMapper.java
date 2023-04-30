package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {UserDtoMapper.class, ItemDtoMapper.class})
public interface BookingDtoMapper {
    @Mapping(target = "start", source = "startTime")
    @Mapping(target = "end", source = "endTime")
    OutgoingBookingDto toDto(Booking booking);

    @Mapping(target = "id", source = "bookingDto.id")
    @Mapping(target = "startTime", source = "bookingDto.start")
    @Mapping(target = "endTime", source = "bookingDto.end")
    Booking toBooking(IncomingBookingDto bookingDto, Item item, User booker);

    @Mapping(target = "bookerId", source = "booker.id")
    ItemBookingDto toItemBookingDto(Booking booking);

}
