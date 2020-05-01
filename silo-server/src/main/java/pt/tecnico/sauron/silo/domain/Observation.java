package pt.tecnico.sauron.silo.domain;

import pt.tecnico.sauron.silo.exceptions.InvalidIdentifierException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Observation implements Serializable {
    private final ObservationType type;
    private final String identifier;
    private final String cameraName;
    private final LocalDateTime datetime;
    private Camera camera;

    public Observation(String cameraName, ObservationType type, String identifier, LocalDateTime datetime) throws InvalidIdentifierException {
        this.type = type;
        this.identifier = identifier;
        this.datetime = datetime;

        if ((type == ObservationType.CAR && !this.checkCarIdentifier(identifier)) ||
            (type == ObservationType.PERSON && !identifier.matches("^\\d+$"))) {
            throw new InvalidIdentifierException(identifier);
        }

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

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public String toString() {
        return "Observation(" + type + "," +identifier + ")@" + datetime.toString() + " by " + cameraName;
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