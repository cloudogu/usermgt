package de.triology.universeadm.robots;

/** Includes the token secret only in the response to successful creation. */
public class CreatedRobotPAT extends RobotPAT {
    private String token;

    public String getToken() { return token; }
}
