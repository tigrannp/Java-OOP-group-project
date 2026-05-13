package Core;

/**
 * Result codes returned by {@link UnitPlacementSession#placeUnit}.
 */
public enum PlacementResult {
    /** The unit was placed successfully. */
    SUCCESS,
    /** The overall unit limit (5) has been reached. */
    EXCEED_TOTAL,
    /** The per-type unit limit (3) has been reached for this unit type. */
    EXCEED_TYPE,
    /** The specified position is outside the valid placement area. */
    OUT_OF_BOUNDS,
    /** Another unit is already on the specified tile. */
    TILE_TAKEN,
    /** The unit type name is not recognized. */
    INVALID_TYPE
}
