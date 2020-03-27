package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionReviewService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;

import java.io.Serializable;
import java.util.List;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private AdministrationService administrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private SuggestionReviewService suggestionReviewService;

    @Autowired
    private TournamentService tournamentService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String username = ((User) authentication.getPrincipal()).getUsername();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(username).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
                case "DEMO.ACCESS":
                    return courseDto.getName().equals("Demo Course");
                default:
                    return false;
            }
        }

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    CourseDto courseDto = administrationService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(username, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, questionService.findQuestionCourse(id).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(username, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(username, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "QUERY.ALTER":
                    return userCanAlterQuery(username, id);
                case "QUERY.ACCESS":
                    return userCanAddAnswerQuery(username, id);
                case "SUGGESTION.AUTHOR":
                    return userIsSuggestionAuthor(username, id);
                case "SUGGESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, suggestionService.getSuggestionCourse(id).getCourseId());
                case "SUGGESTIONREVIEW.RECEPTOR":
                    return userIsSuggestionReviewReceptor(username, id);
                case "SUGGESTIONREVIEW.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, suggestionReviewService.getSuggestionReviewCourse(id).getCourseId());
                case "TOURNAMENT.CREATOR":
                    return userIsTournamentCreator(username, id);
                default: return false;
            }
        }

        return false;
    }

    private boolean userIsSuggestionAuthor(String username, int suggestionId) {
        return suggestionService.getSuggestionUser(suggestionId).getUsername().equals(username);
    }

    private boolean userIsSuggestionReviewReceptor(String username, int suggestionReviewId) {
        return suggestionReviewService.getSuggestionReviewReceptor(suggestionReviewId).getUsername().equals(username);
    }

    private boolean userHasAnExecutionOfTheCourse(String username, int id) {
        return userService.getCourseExecutions(username).stream()
                .anyMatch(course -> course.getCourseId() == id);
    }

    private boolean userHasThisExecution(String username, int id) {
        return userService.getCourseExecutions(username).stream()
                .anyMatch(course -> course.getCourseExecutionId() == id);
    }

    private boolean userCanAlterQuery(String username, int queryId) {
        Integer studentId = userService.findByUsername(username).getId();
        List<QueryDto> queries = queryService.getQueriesByStudent(studentId);
        return queries.stream().anyMatch(queryDto -> queryDto.getId() == queryId);
    }

    private boolean userCanAddAnswerQuery(String username, int queryId) {
        Integer teacherId = userService.findByUsername(username).getId();

        List<QueryDto> queries = queryService.getQueriesInTeachersCourse(teacherId);
        return queries.stream().anyMatch(queryDto -> queryDto.getId() == queryId);
    }

    private boolean userIsTournamentCreator(String username, int tournamentId) {
        return tournamentService.getCreator(tournamentId).getUsername() == username;
    }

     @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
