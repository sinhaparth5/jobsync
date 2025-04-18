package com.jobsync.jobsync;

import com.jobsync.jobsync.model.JobApplication;
import com.jobsync.jobsync.model.TechnicalNote;
import com.jobsync.jobsync.model.User;
import com.jobsync.jobsync.repository.JobApplicationRepository;
import com.jobsync.jobsync.repository.TechnicalNoteRepository;
import com.jobsync.jobsync.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class JobsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobsyncApplication.class, args);
	}

	@Bean
	CommandLineRunner initData(UserRepository userRepository, JobApplicationRepository jobApplicationRepository, TechnicalNoteRepository technicalNoteRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Regular User
			User user = new User("parthsinha", passwordEncoder.encode("password123"), "parth@example.com", List.of("ROLE_USER"));
			userRepository.save(user);

			// Admin User
			User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@example.com", List.of("ROLE_ADMIN"));
			userRepository.save(admin);

			// Job Applications for parthsinha
			jobApplicationRepository.save(new JobApplication("Google", "Software Engineer", "Applied", LocalDate.of(2025, 5, 1), "sample_resume_google.pdf", user));
			jobApplicationRepository.save(new JobApplication("Amazon", "Backend Developer", "Interviewing", LocalDate.of(2025, 4, 20), null, user));
			jobApplicationRepository.save(new JobApplication("Microsoft", "Data Scientist", "Offer", LocalDate.of(2025, 4, 25), "sample_resume_microsoft.pdf", user));

			// Technical Notes for parthsinha
			technicalNoteRepository.save(new TechnicalNote("System Design", "Microservices architecture", "Architecture", user));
			technicalNoteRepository.save(new TechnicalNote("Algorithms", "Binary search implementation", "Coding", user));
			technicalNoteRepository.save(new TechnicalNote("Database", "SQL optimization techniques", "Database", user));

			// Job Application for admin
			jobApplicationRepository.save(new JobApplication("Apple", "Cloud Engineer", "Applied", LocalDate.of(2025, 5, 15), null, admin));

			// Technical Note for admin
			technicalNoteRepository.save(new TechnicalNote("Cloud", "AWS deployment", "Cloud", admin));
		};
	}
}
