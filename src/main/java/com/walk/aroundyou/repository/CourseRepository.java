package com.walk.aroundyou.repository;


import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.walk.aroundyou.domain.Course;

public interface CourseRepository 
			extends JpaRepository<Course, Long>, 
					JpaSpecificationExecutor<Course> {
	
	//*** 특정 컬럼 값을 검색 조건으로 조회 메소드 작성 + 산책로명으로 정렬 ***
	// - 지역, 난이도, 코스거리, 소요시간 등 => findAll(Specification) 활용!
	// 소요시간의 검색 조건은 1시간 이내, 1~2시간 이내, 2~3시간 이내, 3~4시간 이내, 4시간 이상으로 나눴다.
	// -> 메소드의 파라미터로 소요시간 시작과 끝 범위에 대한 "hh:mm:ss" 형식의 값 두 개 들어와야 함.
	///   ex. "00:00:00" - "00:59:00"
	
	// c.signguCn, c.coursLevelNm, c.coursLtCn, c.coursTimeCn
	// 정렬 설정 : order by c.wlkCoursFlagNm ASC, c.wlkCoursNm ASC
	
	// Specification과 Sort를 사용한 findAll 메소드 -> JPARepository에 기본 정의되어 있음.
	//public List<Course> findAll(Specification<Course> spec, Sort sort);
}
