package com.ss.web.app;

import com.ss.web.app.controllers.EnrollmentController;
import com.ss.web.app.model.Student;
import com.ss.web.app.model.Subject;
import com.ss.web.app.service.EnrollmentService;
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
class EnrollmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
    }

    @Test
    void testGetSubjects() throws Exception {
        Long studentId = 1L;
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(101L, "Math", "Mathematics subject"));
        subjects.add(new Subject(102L, "Science", "Science subject"));

        when(enrollmentService.findAllSubjects(studentId)).thenReturn(subjects);

        mockMvc.perform(MockMvcRequestBuilders.get("/enrollment/student/{id}/class", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)) // Assuming the response is a JSON array
                .andExpect(jsonPath("$[0].code").value(101L))
                .andExpect(jsonPath("$[0].title").value("Math"))
                .andExpect(jsonPath("$[0].description").value("Mathematics subject"))
                .andExpect(jsonPath("$[1].code").value(102L))
                .andExpect(jsonPath("$[1].title").value("Science"))
                .andExpect(jsonPath("$[1].description").value("Science subject"));

        verify(enrollmentService, times(1)).findAllSubjects(studentId);
        verifyNoMoreInteractions(enrollmentService);
    }

    @Test
    void testGetStudents() throws Exception {
        Long subjectId = 101L;
        List<Student> students = new ArrayList<>();
        students.add(new Student(1L, "John", "Doe"));
        students.add(new Student(2L, "Jane", "Smith"));

        when(enrollmentService.findAllStudents(subjectId)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/enrollment/class/{id}/students", subjectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)) // Assuming the response is a JSON array
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));

        verify(enrollmentService, times(1)).findAllStudents(subjectId);
        verifyNoMoreInteractions(enrollmentService);
    }
}
