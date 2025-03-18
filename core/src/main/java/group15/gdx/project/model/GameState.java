package group15.gdx.project.model;

/**
 * Represents the various states of the game.
 *
 * <p>This enum defines the different phases that a player can encounter while interacting
 * with the game. The states include the login process, lobby creation or joining, active gameplay,
 * paused game, and post-game review.
 * </p>
 */
public enum GameState {

    /**
     * The state when the user is at the login screen.
     */
    LOGIN_SCREEN_STATE,

    /**
     * The state when the user is greeted with a welcome screen after logging in.
     */
    WELCOME_SCREEN_STATE,

    /**
     * The state when the user is creating a new game or lobby.
     */
    CREATE_GAME_STATE,

    /**
     * The state when the user is joining an existing game or lobby.
     */
    JOIN_GAME_STATE,

    /**
     * The state when the game is in progress.
     */
    ACTIVE_GAME_STATE,

    /**
     * The state when the game is paused.
     */
    PAUSED_GAME_STATE,

    /**
     * The state when the post-game screen is displayed, showing scores and other results.
     */
    POST_GAME_SCREEN_STATE,
}
