package com.hostel.management.common.service;

import com.hostel.management.student.entity.Student;
import com.hostel.management.student.exception.StudentNotFoundException;
import com.hostel.management.student.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonService
{
    private final StudentRepository studentRepository;
    public CommonService(StudentRepository studentRepository)
    {
        this.studentRepository = studentRepository;
    }
    public Student getCurrentStudent()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return studentRepository.findById(username)
                .orElseThrow(()-> new StudentNotFoundException("Student Not Found"));

    }
}
