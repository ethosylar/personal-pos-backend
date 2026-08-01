package site.ethosylar.pos.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse<T> {

	private List<T> content;

	private Integer page;

	private Integer size;

	private Long totalElements;

	private Integer totalPages;

	private Boolean first;

	private Boolean last;

	private Boolean empty;

	public static <T> PageResponse<T> from(Page<T> page) {

		return PageResponse.<T>builder().content(page.getContent()).page(page.getNumber() + 1).size(page.getSize())
				.totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).first(page.isFirst())
				.last(page.isLast()).empty(page.isEmpty()).build();
	}
}