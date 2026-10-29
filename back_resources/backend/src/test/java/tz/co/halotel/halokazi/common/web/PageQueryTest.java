package tz.co.halotel.halokazi.common.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import tz.co.halotel.halokazi.common.error.ApiException;

class PageQueryTest {

    @Test
    void defaults() {
        PageQuery q = PageQuery.of(null, null);
        assertThat(q.page()).isEqualTo(1);
        assertThat(q.size()).isEqualTo(10);
        assertThat(q.offset()).isZero();
    }

    @Test
    void offset() {
        assertThat(PageQuery.of(3, 20).offset()).isEqualTo(40);
    }

    @Test
    void rejectsOutOfRange() {
        assertThatThrownBy(() -> PageQuery.of(0, 10)).isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> PageQuery.of(1, 0)).isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> PageQuery.of(1, 51)).isInstanceOf(ApiException.class);
        assertThat(PageQuery.of(1, 50).size()).isEqualTo(50);
    }

    @Test
    void totalPagesRoundsUp() {
        assertThat(PageResponse.of(List.of(), PageQuery.of(1, 10), 21).totalPages()).isEqualTo(3);
        assertThat(PageResponse.of(List.of(), PageQuery.of(1, 10), 20).totalPages()).isEqualTo(2);
        assertThat(PageResponse.of(List.of(), PageQuery.of(1, 10), 0).totalPages()).isZero();
    }
}
