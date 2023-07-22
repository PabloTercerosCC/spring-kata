package com.ss.web.app;

import com.ss.web.app.controllers.StudentController;
import com.ss.web.app.model.Student;
import com.ss.web.app.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class StudentControllerTest {

	private MockMvc mockMvc;

	@Mock
	private StudentService studentService;

	@InjectMocks
	private StudentController studentController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
	}

	@Test
	void testGetAllStudents() throws Exception {
		List<Student> students = new ArrayList<>();
		students.add(new Student(1L, "John", "Doe"));
		students.add(new Student(2L, "Jane", "Smith"));

		when(studentService.findAll()).thenReturn(students);

		mockMvc.perform(MockMvcRequestBuilders.get("/students"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2)) // Assuming the response is a JSON array
				.andExpect(jsonPath("$[0].name" +
						"").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe"))
				.andExpect(jsonPath("$[1].name" +
						"").value("Jane"))
				.andExpect(jsonPath("$[1].lastName").value("Smith"));

		verify(studentService, times(1)).findAll();
		verifyNoMoreInteractions(studentService);
	}

	@Test
	void testCreateStudent() throws Exception {
		Student newStudent = new Student(null, "Alice", "Johnson");
		Student savedStudent = new Student(3L, "Alice", "Johnson");

		when(studentService.addStudent(any(Student.class))).thenReturn(savedStudent);

		mockMvc.perform(MockMvcRequestBuilders.post("/students/create")
						.content("{\"name" +
								"\":\"Alice\",\"lastName\":\"Johnson\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(3L))
				.andExpect(jsonPath("$.name" +
						"").value("Alice"))
				.andExpect(jsonPath("$.lastName").value("Johnson"));

		verify(studentService, times(1)).addStudent(any(Student.class));
		verifyNoMoreInteractions(studentService);
	}

	@Test
	void testDeleteStudent() throws Exception {
		List<Student> students = new ArrayList<>();
		students.add(new Student(1L, "John", "Doe"));
		students.add(new Student(2L, "Jane", "Smith"));
		students.add(new Student(3L, "Alice", "Johnson"));

		when(studentService.findAll()).thenReturn(students);

		mockMvc.perform(MockMvcRequestBuilders.delete("/students/delete")
						.content("{\"id\":3,\"name\":\"Alice\",\"lastName\":\"Johnson\"}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// After deletion, the list should no longer contain the deleted student
		mockMvc.perform(MockMvcRequestBuilders.get("/students"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].name").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe"))
				.andExpect(jsonPath("$[1].name").value("Jane"))
				.andExpect(jsonPath("$[1].lastName").value("Smith"));

		verify(studentService, times(1)).delete(any(Student.class));
		verify(studentService, times(1)).findAll();
		verifyNoMoreInteractions(studentService);
	}
}
