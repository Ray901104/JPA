package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    void order() {
        //given
        Member member = createMember("memberA", new Address("Seoul", "Moonjung", "123-123"));

        Book book = createBook("JPA가이드", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order ordered = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, ordered.getStatus()); //상품 주문시 상태는 ORDER
        assertEquals(1, ordered.getOrderItems().size()); //주문한 상품 종류 수가 정확한지
        assertEquals(10000 * orderCount, ordered.getTotalPrice()); //주문 가격은 가격 * 수량이 맞는지
        assertEquals(8, book.getStockQuantity()); //주문 수량만큼 재고가 줄었는지
    }

    @Test
    void cancel() {
        //given
        Member member = createMember("memberA", new Address("Seoul", "Moonjung", "123-123"));
        Book book = createBook("JPA가이드", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus()); //주문 취소시 상태는 CANCEL
        assertEquals(10, book.getStockQuantity()); //주문이 취소된 상품은 그만큼 재고가 증가해야 한다.
    }

    @Test
    @DisplayName("재고 수량 초과 시 NotEnoughStockException 예외 발생")
    void orderStockValidation() {
        //given
        Member member = createMember("memberA", new Address("Seoul", "Moonjung", "123-123"));
        Book book = createBook("JPA가이드", 10000, 10);

        //when
        int orderCount = 11;

        //then
        Assertions.assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);
    }

    private Member createMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}
