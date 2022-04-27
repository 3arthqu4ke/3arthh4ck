package me.earth.earthhack.impl.modules.render.modeltotem;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ListSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.model.IModel;
import me.earth.earthhack.impl.util.render.model.Mesh;
import me.earth.modeltotem.ModelTotemFileManager;

public class ModelTotem extends Module
{

    protected final Setting<IModel> fileSettingTest =
            register(new ListSetting<>("Model", ModelTotemFileManager.INSTANCE.getInitialModel(), ModelTotemFileManager.INSTANCE.getModels()));
    
    protected final Setting<Boolean> debug =
            register(new BooleanSetting("Debug", false));

    protected final Setting<Float> rotateHorizontal =
            register(new NumberSetting<>("RotateX", 0.0f, 0.0f, 360.0f));
    protected final Setting<Float> rotateVertical =
            register(new NumberSetting<>("RotateY", 0.0f, 0.0f, 360.0f));
    protected final Setting<Float> rotateZ =
            register(new NumberSetting<>("RotateZ", 0.0f, 0.0f, 360.0f));
    protected final Setting<Double> translateX =
            register(new NumberSetting<>("TranslateX", 0.0, -1.0, 1.0));
    protected final Setting<Double> translateY =
            register(new NumberSetting<>("TranslateY", 0.0, -1.0, 1.0));
    protected final Setting<Double> translateZ =
            register(new NumberSetting<>("TranslateZ", 0.0, -1.0, 1.0));
    protected final Setting<Double> scaleX =
            register(new NumberSetting<>("ScaleX", 1.0, 0.00001, 10.0));
    protected final Setting<Double> scaleY =
            register(new NumberSetting<>("ScaleY", 1.0, 0.00001, 10.0));
    protected final Setting<Double> scaleZ =
            register(new NumberSetting<>("ScaleZ", 1.0, 0.00001, 10.0));
    /*protected final Setting<Nameable> model =
                register(new ListSetting("RootDir", "None!"));*/
    protected final Setting<Float> popRotateHorizontal =
            register(new NumberSetting<>("PopRotateX", 0.0f, 0.0f, 360.0f));
    protected final Setting<Float> popRotateVertical =
            register(new NumberSetting<>("PopRotateY", 0.0f, 0.0f, 360.0f));
    protected final Setting<Float> popRotateZ =
            register(new NumberSetting<>("PopRotateZ", 0.0f, 0.0f, 360.0f));
    protected final Setting<Double> popTranslateX =
            register(new NumberSetting<>("PopTranslateX", 0.0, -1.0, 1.0));
    protected final Setting<Double> popTranslateY =
            register(new NumberSetting<>("PopTranslateY", 0.0, -1.0, 1.0));
    protected final Setting<Double> popTranslateZ =
            register(new NumberSetting<>("PopTranslateZ", 0.0, -1.0, 1.0));
    protected final Setting<Double> popScaleX =
            register(new NumberSetting<>("PopScaleX", 1.0, 0.0001, 10.0));
    protected final Setting<Double> popScaleY =
            register(new NumberSetting<>("PopScaleY", 1.0, 0.0001, 10.0));
    protected final Setting<Double> popScaleZ =
            register(new NumberSetting<>("PopScaleZ", 1.0, 0.0001, 10.0));

    private Mesh[] modelMeshes;


    public ModelTotem()
    {
        super("ModelTotem", Category.Render);
        this.listeners.add(new ListenerRenderItemInFirstPerson(this));
        this.listeners.add(new ListenerRenderItemActivation(this));
    }

}
