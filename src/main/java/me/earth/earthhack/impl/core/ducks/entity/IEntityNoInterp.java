package me.earth.earthhack.impl.core.ducks.entity;

public interface IEntityNoInterp
{
    double getNoInterpX();

    double getNoInterpY();

    double getNoInterpZ();

    void setNoInterpX(double x);

    void setNoInterpY(double y);

    void setNoInterpZ(double z);

    int getPosIncrements();

    void setPosIncrements(int posIncrements);

    float getNoInterpSwingAmount();

    float getNoInterpSwing();

    float getNoInterpPrevSwing();

    void setNoInterpSwingAmount(float noInterpSwingAmount);

    void setNoInterpSwing(float noInterpSwing);

    void setNoInterpPrevSwing(float noInterpPrevSwing);

    /**
     * @return <tt>true</tt> unless this Entity is an EntityPlayerSP.
     */
    boolean isNoInterping();

    void setNoInterping(boolean noInterping);

}
