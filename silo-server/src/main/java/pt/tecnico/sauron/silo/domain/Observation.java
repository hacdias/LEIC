package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Observation {
    private ObservationType type;
    private String identifier;
    private String cameraName;
    private LocalDateTime datetime;
    private Camera camera;

    private Observation (ObservationType type, String identifier, LocalDateTime datetime) throws InvalidIdentifierException {
        this.type = type;
        this.identifier = identifier;
        this.datetime = datetime;

        if ((type == ObservationType.CAR && !this.checkCarIdentifier(identifier)) ||
            (type == ObservationType.PERSON && !identifier.matches("^\\d+$"))) {
            throw new InvalidIdentifierException(identifier);
        }
    }

    public Observation(Camera camera, ObservationType type, String identifier, LocalDateTime datetime) throws InvalidIdentifierException {
        this(type, identifier, datetime);
        this.camera = camera;
    }

    public Observation(String cameraName, ObservationType type, String identifier, LocalDateTime datetime) throws InvalidIdentifierException {
        this(type, identifier, datetime);
        this.cameraName = cameraName;
    }

    private Boolean checkCarIdentifier(String identifier) {
        Integer charGroups = new PatternMatcher("[A-Z]{2}").matches(identifier);
        Integer intGroups = new PatternMatcher("[0-9]{2}").matches(identifier);

        return charGroups >= 1 && intGroups >= 1 && intGroups + charGroups == 3;
    }

    public ObservationType getType() {
        return type;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Camera getCamera() {
        return camera;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setType(ObservationType type) {
        this.type = type;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public String toString() {
        return "Observation(" + type + "," +identifier + ")@" + datetime.toString() + " by " + camera.toString();
    }

    private static class PatternMatcher {
        Pattern pattern;

        PatternMatcher(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        public Integer matches(String string) {
            Matcher matcher = pattern.matcher(string);
            Integer count = 0;
            while (matcher.find()) {
                count++;
            }
            return count;
        }
    }
}