package com.gymrattrax.scheduler.model;

public class ExerciseItem {
    private String name;
    private ExerciseType type;
    private ExerciseName.Abs abs;
    private ExerciseName.Arms arms;
    private ExerciseName.Cardio cardio;
    private ExerciseName.Legs legs;

    private ExerciseItem(){}
    public ExerciseItem(ExerciseName.Abs abs) {
        this.name = abs.toString();
        this.abs = abs;
        this.type = ExerciseType.ABS;
    }
    public ExerciseItem(ExerciseName.Arms arms) {
        this.name = arms.toString();
        this.arms = arms;
        this.type = ExerciseType.ARMS;
    }
    public ExerciseItem(ExerciseName.Cardio cardio) {
        this.name = cardio.toString();
        this.cardio = cardio;
        this.type = ExerciseType.CARDIO;
    }
    public ExerciseItem(ExerciseName.Legs legs) {
        this.name = legs.toString();
        this.legs = legs;
        this.type = ExerciseType.LEGS;
    }

    public String getName() {
        return name;
    }

    public ExerciseType getType() {
        return type;
    }

    public ExerciseName.Abs getAbs() {
        return abs;
    }

    public ExerciseName.Arms getArms() {
        return arms;
    }

    public ExerciseName.Cardio getCardio() {
        return cardio;
    }

    public ExerciseName.Legs getLegs() {
        return legs;
    }
}