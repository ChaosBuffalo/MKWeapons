package com.chaosbuffalo.mkweapons.items.weapon.effects;



public abstract class SwingWeaponEffect implements IWeaponEffect {
    private final int numberOfHits;
    private final double perHit;

    public SwingWeaponEffect(int numberOfHits, double perHit){
        this.numberOfHits = numberOfHits;
        this.perHit = perHit;
    }

    public double getPerHit() {
        return perHit;
    }

    public int getNumberOfHits() {
        return numberOfHits;
    }
}
