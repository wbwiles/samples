


/**
 * LatitudeLongitudeDistnaceCalulator.java
 */
public class LatitudeLongitudeDistnaceCalulator {
    private static final double RADIUS_MILES = 3963.0;
    private static final double PI_DIVIDE_180 = 57.2958;
    private static final LoggerUtil LOG = LoggerUtil.getLogger(LatitudeLongitudeDistnaceCalulator.class.getName());

    private static final int DEGREES_POSITION = 0;
    private static final int MINUTES_POSITION = 1;
    private static final int SECONDS_POSITION = 2;

    private Latitude originLatitude = new Latitude();
    private Latitude destLatitude = new Latitude();
    private Longitude originLongitude = new Longitude();
    private Longitude destLongitude = new Longitude();

    protected double parseLatLongString(String origLat) {

        LOG.debug("LATITUDE = " + origLat);
        String[] degreeMinutes = origLat.split("\\.");
        LOG.debug("After Split..." + degreeMinutes.length + " Value = " + degreeMinutes);
        int degrees = Integer.parseInt(degreeMinutes[DEGREES_POSITION]);
        int minutes = Integer.parseInt(degreeMinutes[MINUTES_POSITION]);
        String sec = degreeMinutes[SECONDS_POSITION];
        int seconds = 0;
        if (sec.charAt(sec.length() - 1) == 'N' || sec.charAt(sec.length() - 1) == 'S' || sec.charAt(sec.length() - 1) == 'E' || sec.charAt(sec.length() - 1) == 'W') {
            seconds = Integer.parseInt(degreeMinutes[SECONDS_POSITION].substring(0, degreeMinutes[SECONDS_POSITION].length() - 1));
        } else {
            throw new ServiceException(ServiceErrorType.POLICY_EXECUTION_TRANSLATION_ERROR);

        }
        double value = degrees + (((double) minutes) / TPEConstants.SIXTY_INT) + (((double) seconds) / TPEConstants.THREE_THOUSAND_SIX_HUNDRED_INT);
        if (sec.charAt(sec.length() - 1) == 'S' || sec.charAt(sec.length() - 1) == 'W') {
            value = value * -1;
        }
        LOG.debug("Setting value for " + origLat + " As value = " + value);
        return value;

    }

    protected double getDistanceInMiles() {
        double distance;
        distance = RADIUS_MILES
                * Math.acos(Math.sin(originLatitude.getDoubleValue() / PI_DIVIDE_180) * Math.sin(destLatitude.getDoubleValue() / PI_DIVIDE_180) + Math.cos(originLatitude.getDoubleValue() / PI_DIVIDE_180)
                        * Math.cos(destLatitude.getDoubleValue() / PI_DIVIDE_180) * Math.cos(destLongitude.getDoubleValue() / PI_DIVIDE_180 - originLongitude.getDoubleValue() / PI_DIVIDE_180));
        LOG.debug("DIStance = " + distance);

        return distance;
    }

    /**
     * @param origLat origLat
     * @param origLong origLong
     * @param destLat destLat
     * @param destLong destLong
     * @return double distance
     */
    public double getAirportDistance(String origLat, String origLong, String destLat, String destLong) {
        originLatitude.setRepresentation(parseLatLongString(origLat));
        originLongitude.setRepresentation(parseLatLongString(origLong));
        destLatitude.setRepresentation(parseLatLongString(destLat));
        destLongitude.setRepresentation(parseLatLongString(destLong));
        return getDistanceInMiles();
    }
}
