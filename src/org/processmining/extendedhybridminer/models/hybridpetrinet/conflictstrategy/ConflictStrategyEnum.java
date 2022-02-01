package org.processmining.extendedhybridminer.models.hybridpetrinet.conflictstrategy;

public enum ConflictStrategyEnum {
	DEFAULT_CONFLICT_STRATEGY, // DefaultConflictStrategyNonOpt
	DEFAULT_CONFLICT_STRATEGY_OPT, // DefaultConflictStrategyOpt
	NO_CONFLICT_STRATEGY, // NoConflictStrategy
	DEFAULT_CONFLICT_STRATEGY_OPT_NEW, // DefaultConflictStrategyOptNewOutput if outputNodes.size()>inputNodes.size()
	// else: DefaultConflictStrategyOptNew
	CONFLICT_STRATEGY_LONG_DEP,
	LONG_DEP_CONFLICT_STRATEGY,
	MINIMAL_PLACES,
	MINIMAL_PLACES_BACKWARDS
}