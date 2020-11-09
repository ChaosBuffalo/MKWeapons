package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;



public abstract class SwingMeleeWeaponEffect implements IMeleeWeaponEffect {
    private final int numberOfHits;
    private final double perHit;

    public SwingMeleeWeaponEffect(int numberOfHits, double perHit){
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
