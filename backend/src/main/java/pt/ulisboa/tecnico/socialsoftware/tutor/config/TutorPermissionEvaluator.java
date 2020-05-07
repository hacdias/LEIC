package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.AnswerQueryService;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
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
    private CourseService courseService;

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
    private AnswerQueryService answerQueryService;
    
    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private SuggestionReviewService suggestionReviewService;

    @Autowired
    private TournamentService tournamentService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        int userId = ((User) authentication.getPrincipal()).getId();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(userId).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
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
                    CourseDto courseDto = courseService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(userId, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, questionService.findQuestionCourse(id).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(userId, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(userId, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "QUERY.ACCESS":
                    return userCanAddAnswerQuery(userId, id);
                case "QUERY.ALTER":
                    return userCanAlterQuery(userId, id);
                case "ANSWER.QUERY.ACCESS":
                    return userCanAddFurtherClarification(userId, id);
                case "ANSWER.QUERY.ALTER":
                    return userCanAlterAnswerQuery(userId, id);
                case "SUGGESTION.AUTHOR":
                    return userIsSuggestionAuthor(userId, id);
                case "SUGGESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, suggestionService.getSuggestionCourse(id).getCourseId());
                case "SUGGESTIONREVIEW.RECEPTOR":
                    return userIsSuggestionReviewReceptor(userId, id);
                case "SUGGESTIONREVIEW.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, suggestionReviewService.getSuggestionReviewCourse(id).getCourseId());
                case "TOURNAMENT.CREATOR":
                    return userIsTournamentCreator(userId, id);
                default: return false;
            }
        }

        return false;
    }

    private boolean userIsSuggestionAuthor(int userId, int suggestionId) {
        return suggestionService.getSuggestionUser(suggestionId).getId() == userId;
    }

    private boolean userIsSuggestionReviewReceptor(int userId, int suggestionReviewId) {
        return suggestionReviewService.getSuggestionReviewReceptor(suggestionReviewId).getId() == userId;
    }

    private boolean userHasAnExecutionOfTheCourse(int userId, int courseId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseId() == courseId);
    }

    private boolean userHasThisExecution(int userId, int courseExecutionId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseExecutionId() == courseExecutionId);
    }

    private boolean userCanAlterQuery(int userId, int queryId) {
        List<QueryDto> queries = queryService.getQueriesByStudent(userId);
        return queries.stream().anyMatch(queryDto -> queryDto.getId() == queryId);
    }

    private boolean userCanAlterAnswerQuery(int userId, int answerQueryId) {
        List<AnswerQueryDto> answers = answerQueryService.getAnswersByTeacher(userId);
        return answers.stream().anyMatch(answerQueryDto -> answerQueryDto.getId() == answerQueryId);
    }

    private boolean userCanAddAnswerQuery(int userId, int queryId) {
        List<QueryDto> queries = queryService.getQueriesInCourse(userId);
        return queries.stream().anyMatch(queryDto -> queryDto.getId() == queryId);
    }

    private boolean userCanAddFurtherClarification(int userId, int answerQueryId) {
        AnswerQueryDto answerQueryDto = answerQueryService.findAnswerQueryById(answerQueryId);

        while (answerQueryDto.getQueryId() == null) {
            answerQueryDto = answerQueryService.findAnswerQueryById(answerQueryDto.getAnswerQueryId());
        }

        return userCanAddAnswerQuery(userId, answerQueryDto.getQueryId());
    }

    private boolean userIsTournamentCreator(int userId, int tournamentId) {
        return tournamentService.getCreator(tournamentId).getId() == userId;
    }

     @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
