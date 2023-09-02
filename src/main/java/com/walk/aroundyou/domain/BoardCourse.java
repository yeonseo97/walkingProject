package com.walk.aroundyou.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="board_course")
public class BoardCourse {

//	Reason:
//		SQL Error [1452] [23000]: (conn=107) 
//		Cannot add or update a child row: a foreign key constraint fails 
//		(`wayproject`.`board_course`, CONSTRAINT `FKmwukxg5sn3bm27yapw3afrmt4` FOREIGN KEY (`course_id`) 
//				REFERENCES `course` (`course_id`))
	
	
	@Id
	@Column(name="board_course_id", columnDefinition="bigint", nullable=false)
	private long boardCourseId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board boardId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course courseId;
	
}