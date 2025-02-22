package com.walk.aroundyou.controller;


import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.walk.aroundyou.domain.Course;
import com.walk.aroundyou.dto.CourseResponseDTO;
import com.walk.aroundyou.dto.IBoardListResponse;
import com.walk.aroundyou.repository.CourseRepository;
import com.walk.aroundyou.service.CourseLikeService;
import com.walk.aroundyou.service.CourseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
public class CourseViewController {

	private final CourseService courseService;
	private final CourseLikeService courseLikeService;
	
	// 페이지네이션 사이즈
	private final static int PAGINATION_SIZE = 5;
	
	/**
	 * [산책로 목록 조회페이지] 전체 목록 조회
	 *  ↳ REST로는 다수의 파라미터를 넘겨서 검색 조건이 되도록 했다.
	 *    뷰에서는 경로에 나타나지 않고 AJAX로 처리!
	 *    이 메소드는 ajax와 관계없이 전체 조회 첫 페이지 요청
	 */
	@GetMapping("/course")
	public String getCourses(
			@RequestParam(name="sort", required= false) String sort,
			@RequestParam(name="page", required= false, 
			defaultValue = "0") int currentPage, 
			Model model) {
		
		Page<CourseResponseDTO> coursePage = 
				courseService.findAll(sort, currentPage);
		
		// pagination 설정
		int totalPages = coursePage.getTotalPages();
		int pageStart = getPageStart(currentPage, totalPages);
		int pageEnd = 
				(PAGINATION_SIZE < totalPages)? 
						pageStart + PAGINATION_SIZE - 1
						:totalPages;
		model.addAttribute("lastPage", totalPages);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("pageStart", pageStart);
		model.addAttribute("pageEnd", pageEnd);
		
		// 산책로 리스트 저장
		model.addAttribute("courses", coursePage);  
		
		// courseList.html라는 뷰 조회
		return "courseList";
	}

	/**
	 * [산책로 목록 조회페이지] 검색 조건에 따른 전체 목록 조회
	 * : 메뉴를 클릭했을 때 나오는 첫 화면과 별개로 조건에 따른 AJAX 요청 경로 처리
	 */
	@GetMapping("/course/search")
	public String getCoursesByConditions(
			@RequestParam(name = "region", required = false) String region, 
			@RequestParam(name = "level", required = false) String level, 
			@RequestParam(name = "time", required = false) String time,
			@RequestParam(name = "distance", required = false) String distance, 
			@RequestParam(name = "searchTargetAttr", required = false) String searchTargetAttr,
			@RequestParam(name = "searchKeyword", required = false) String searchKeyword, 
			@RequestParam(name="sort", required= false) String sort,
			@RequestParam(name="page", required= false, 
			defaultValue = "0") int currentPage,
			Model model) {
			log.info("level : " + level);
			log.info("searchKeyword : " + searchKeyword);
			String startTime = null;
			String endTime = null; 
			
			if(time == null) {
				
			} else if(time.equals("1")) {
				startTime = "00:00:00";
				endTime = "00:59:00";
		    } else if (time.equals("2")) {
		    	startTime = "01:00:00";
		    	endTime = "01:59:00";
		    } else if (time.equals("3")) {
		    	startTime = "02:00:00";
		    	endTime = "02:59:00";
		    } else if (time.equals("4")) {
		    	startTime = "03:00:00";
		    	endTime = "03:59:00";
		    } else if (time.equals("5")) {
		    	startTime = "04:00:00";
		    	endTime = "99:00:00";
		    }
		
		Page<CourseResponseDTO> coursePage = 
				courseService.findAllByCondition(
				region, level, distance, startTime, endTime,
				searchTargetAttr, searchKeyword, sort, currentPage);
		
		// pagination 설정
		// 헷갈리지 말자 : currentPage는 0부터 시작!
		int totalPages = coursePage.getTotalPages();
		int pageStart = getPageStart(currentPage, totalPages);
		int pageEnd = 
				(PAGINATION_SIZE < totalPages)? 
						pageStart + PAGINATION_SIZE - 1
						:totalPages;
		model.addAttribute("lastPage", totalPages);
		// 이전, 이후 버튼에 문제있어서 currentPage + 1 값을 수정했다.
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("pageStart", pageStart);
		model.addAttribute("pageEnd", pageEnd);
		
		// 산책로 리스트 저장
		model.addAttribute("courses", coursePage);  
		
		// 파라미터 값을 모델에 저장 (페이지네이션에 쓰임) - 알아요 쓸데없이 긴 거ㅠㅠ
		model.addAttribute("region", region);
		model.addAttribute("level", level);
		model.addAttribute("distance", distance);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("searchTargetAttr", searchTargetAttr);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("sort", sort);
		
		// courseList.html라는 뷰 조회
		return "courseList";
	}

	
	/**
	 * [산책로 상세페이지] 유저 정보 기반해서 서비스 사용 가능
	 * + 연서님이 작성한 게시판 페이징 기능 관련 코드 추가
	 */
	@GetMapping("/course/{courseId}")
	public String getCourse(
			@PathVariable Long courseId, 
			//Principal principal,
			@RequestParam(name = "page", required=false, defaultValue="0") int currentPage,
	        @RequestParam(name = "sort", required= false, defaultValue = "boardId") String sort,
			Model model) {
		CourseResponseDTO courseResponseDTO = 
				courseService.findByIdWithCounts(courseId);
		model.addAttribute("course", courseResponseDTO);
		model.addAttribute("courseId", courseId);
		
		//String userId = principal.getName(); // 실제 로그인한 유저 정보
		String userId = "wayid1";          // 테스트용. 직접 부여
		model.addAttribute("userId", userId);
		
		// 조회한 좋아요 상태 확인
		boolean isLiked = courseLikeService.isCourseLiked(userId, courseId);
		model.addAttribute("isLiked", isLiked);
		
		// 게시글 출력 용도
		Page<IBoardListResponse> courseBoardList = 
		         courseService.findBoardAndCntByCourseId(courseId, currentPage, sort);
		model.addAttribute("courseBoardList", courseBoardList);
		      
	    // pagination 설정
	    int totalPages = courseBoardList.getTotalPages();
	    int pageStart = getPageStart(currentPage, totalPages);
	    int pageEnd = 
	            (PAGINATION_SIZE < totalPages)? 
	                  pageStart + PAGINATION_SIZE - 1
	                  :totalPages;
	    model.addAttribute("lastPage", totalPages);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("pageStart", pageStart);
	    model.addAttribute("pageEnd", pageEnd);
	    model.addAttribute("sort", sort);
		
		return "course";
	}
	
	/**
	 *  pagination의 첫번째 숫자 얻는 메소드
	 */
	private int getPageStart(int currentPage, int totalPages) {
		int result = 1; 
		if(totalPages < currentPage + (int)Math.floor(PAGINATION_SIZE/2)) {
			// 시작페이지의 최소값은 1!
			result = Math.max(1, totalPages - PAGINATION_SIZE + 1);
		} else if (currentPage > (int)Math.floor(PAGINATION_SIZE/2)) {
			result = currentPage - (int)Math.floor(PAGINATION_SIZE/2) + 1;
		}
		return result;
	}
	
	/**
	 * [관리자 페이지] 산책로 데이터 관리 - 산책로 목록 조회
	 * : 관리자만 볼 수 있는 탭의 산책로 데이터 관리 메뉴에서는
	 *   사용자와 달리 더 심플하게 구성되어 있다.
	 */
	@GetMapping("/admin/courses")
	public String adminGetCourses(
			@RequestParam(name = "region", required = false) String region, 
			@RequestParam(name = "level", required = false) String level, 
			@RequestParam(name = "time", required = false) String time,
			@RequestParam(name = "distance", required = false) String distance, 
			@RequestParam(name = "searchTargetAttr", required = false) String searchTargetAttr,
			@RequestParam(name = "searchKeyword", required = false) String searchKeyword, 
			@RequestParam(name="sort", required= false) String sort,
			@RequestParam(name="page", required= false, 
			defaultValue = "0") int currentPage,
			Model model) {
			
			String startTime = null;
			String endTime = null; 
			
			if(time == null) {
				
			} else if(time.equals("1")) {
				startTime = "00:00:00";
				endTime = "00:59:00";
		    } else if (time.equals("2")) {
		    	startTime = "01:00:00";
		    	endTime = "01:59:00";
		    } else if (time.equals("3")) {
		    	startTime = "02:00:00";
		    	endTime = "02:59:00";
		    } else if (time.equals("4")) {
		    	startTime = "03:00:00";
		    	endTime = "03:59:00";
		    } else if (time.equals("5")) {
		    	startTime = "04:00:00";
		    	endTime = "99:00:00";
		    }
			
		Page<CourseResponseDTO> coursePage = 
				courseService.findAllByCondition(
				region, level, distance, startTime, endTime,
				searchTargetAttr, searchKeyword, 
				sort, currentPage);
		
		// pagination 설정
		// 헷갈리지 말자 : currentPage는 0부터 시작!
		int totalPages = coursePage.getTotalPages();
		int pageStart = getPageStart(currentPage, totalPages);
		int pageEnd = 
				(PAGINATION_SIZE < totalPages)? 
						pageStart + PAGINATION_SIZE - 1
						:totalPages;
		model.addAttribute("lastPage", totalPages);
		// 이전, 이후 버튼에 문제있어서 currentPage + 1 값을 수정했다.
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("pageStart", pageStart);
		model.addAttribute("pageEnd", pageEnd);
		
		// 산책로 리스트 저장
		model.addAttribute("courses", coursePage);  
		
		// 파라미터 값을 모델에 저장 (페이지네이션에 쓰임) - 알아요 쓸데없이 긴 거ㅠㅠ
		model.addAttribute("region", region);
		model.addAttribute("level", level);
		model.addAttribute("distance", distance);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		model.addAttribute("searchTargetAttr", searchTargetAttr);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("sort", sort);
		
		// courseList.html라는 뷰 조회
		return "adminCourseList";
	}

	/**
	 * [관리자 페이지] 산책로 데이터 관리 - 상세 조회 페이지
	 */
	@GetMapping("/admin/courses/{id}")
	public String adminGetCourse(@PathVariable Long id, Model model) {
		CourseResponseDTO courseResponseDTO = courseService.findByIdWithCounts(id);
		model.addAttribute("course", courseResponseDTO);
		
		return "adminCourse";
	}

	/**
	 * [관리자 페이지] 산책로 데이터 관리 - 산책로 생성
	 * 수정 요청을 /admin/courses/{id} POST로 따로 만들까 고민 중
	 * 일단 수정 요청에 해당하는 메소드부터 만들자
	 */
//	@GetMapping("/admin/courses/new-course")
//	public String adminNewCourse(Model model) {
//		model.addAttribute("course", new CourseResponseDTO());
//		
//		// 생성·수정 뷰 똑같게 설정
//		return "adminInsertCourse";
//	}
	
	/**
	 * [관리자 페이지] 산책로 데이터 관리 - 산책로 수정
	 * @PatchMapping 어노테이션은 어떻게 쓰는 거지?
	 */
	@GetMapping("/admin/courses/update/{id}")
	public String adminNewCourse(
			@PathVariable Long id, Model model) {
		Course course = courseService.findById(id);
		model.addAttribute("course", new CourseResponseDTO(course));
		
		// 생성·수정 뷰 똑같게 설정
		return "adminUpdateCourse";
	}
}

	
