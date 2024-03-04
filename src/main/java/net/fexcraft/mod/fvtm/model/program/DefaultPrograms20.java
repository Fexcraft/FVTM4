package net.fexcraft.mod.fvtm.model.program;

import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.mod.fvtm.data.attribute.AttrFloat;
import net.fexcraft.mod.fvtm.data.vehicle.WheelSlot;
import net.fexcraft.mod.fvtm.function.part.WheelFunction;
import net.fexcraft.mod.fvtm.model.ModelGroup;
import net.fexcraft.mod.fvtm.model.ModelRenderData;
import net.fexcraft.mod.fvtm.model.Program;
import net.fexcraft.mod.fvtm.render.Renderer120;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import static net.fexcraft.mod.fvtm.render.Renderer120.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DefaultPrograms20 {

	public static void init(){
		ModelGroup.PROGRAMS.add(new Program() {
			public String id(){
				return "fvtm:rgb_primary";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				if(data.color != null) Renderer120.setColor(data.color.getPrimaryColor());
			}

			public void post(ModelGroup list, ModelRenderData data){
				Renderer120.resetColor();
			}
		});
		ModelGroup.PROGRAMS.add(new Program() {
			public String id(){
				return "fvtm:rgb_secondary";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				if(data.color != null) Renderer120.setColor(data.color.getSecondaryColor());
			}

			public void post(ModelGroup list, ModelRenderData data){
				Renderer120.resetColor();
			}
		});
		ModelGroup.PROGRAMS.add(new RGBCustom(new float[]{ 1, 1, 1 }));
		ModelGroup.PROGRAMS.add(new RGBChannel("custom"));
		ModelGroup.PROGRAMS.add(new Program() {
			public String id(){
				return "fvtm:hide";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				list.visible = false;
			}

			@Override
			public void post(ModelGroup list, ModelRenderData data){
				list.visible = true;
			}
		});
		ModelGroup.PROGRAMS.add(new Program() {
			private WheelSlot slot;
			private AttrFloat attr = null;
			private float am;

			public String id(){
				return "fvtm:wheel_auto_all";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				pushPose();
				slot = data.part.getFunction(WheelFunction.class, "fvtm:wheel").getWheelPos(data.vehicle);
				if(slot != null && slot.steering){
					attr = (AttrFloat)data.vehicle.getAttribute("steering_angle");
					am = attr.initial + data.partialticks * (attr.value - attr.initial);
					rotateDeg(-am, AY);
				}
				rotateDeg(-data.vehicle.getAttribute("wheel_angle").asFloat(), AX);
				if(slot != null && slot.mirror) rotateRad(Static.rad180, AY);
			}

			public void post(ModelGroup list, ModelRenderData data){
				Renderer120.popPose();
			}
		});
		ModelGroup.PROGRAMS.add(new Program() {
			private WheelSlot slot;

			public String id(){
				return "fvtm:wheel_auto_steering";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				pushPose();
				slot = data.part.getFunction(WheelFunction.class, "fvtm:wheel").getWheelPos(data.vehicle);
				if(slot != null && slot.mirror) rotateRad(Static.rad180, AY);
				if(slot != null && slot.steering) rotateDeg(data.vehicle.getAttribute("steering_angle").asFloat(), AY);
			}

			public void post(ModelGroup list, ModelRenderData data){
				popPose();
			}
		});
		ModelGroup.PROGRAMS.add(new Program() {
			private WheelSlot slot;

			public String id(){
				return "fvtm:wheel_auto_all_opposite";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				pushPose();
				slot = data.part.getFunction(WheelFunction.class, "fvtm:wheel").getWheelPos(data.vehicle);
				if(slot != null && slot.steering) rotateDeg(-data.vehicle.getAttribute("steering_angle").asFloat(), AY);
				rotateDeg(data.vehicle.getAttribute("wheel_angle").asFloat(), AX);
				if(slot != null && slot.mirror) rotateRad(Static.rad180, AY);
			}

			public void post(ModelGroup list, ModelRenderData data){
				popPose();
			}
		});
		ModelGroup.PROGRAMS.add(new Program() {
			private WheelSlot slot;

			public String id(){
				return "fvtm:wheel_auto_steering_opposite";
			}

			public void pre(ModelGroup list, ModelRenderData data){
				pushPose();
				slot = data.part.getFunction(WheelFunction.class, "fvtm:wheel").getWheelPos(data.vehicle);
				if(slot != null && slot.mirror) rotateRad(Static.rad180, AY);
				if(slot != null && slot.steering) rotateDeg(-data.vehicle.getAttribute("steering_angle").asFloat(), AY);
			}

			public void post(ModelGroup list, ModelRenderData data){
				popPose();
			}
		});
	}

	public static class RGBCustom implements Program {

		private Vec3f color = new Vec3f();

		public RGBCustom(float[] col){
			color.x = col[0];
			color.y = col[1];
			color.z = col[2];
		}

		@Override
		public String id(){
			return "fvtm:rgb_custom";
		}

		@Override
		public void pre(ModelGroup list, ModelRenderData data){
			Renderer120.setColor(color);
		}

		@Override
		public void post(ModelGroup list, ModelRenderData data){
			Renderer120.resetColor();
		}

		@Override
		public Program parse(String[] args){
			return new RGBCustom(new RGB(args[0]).toFloatArray());
		}

	}

	public static class RGBChannel implements Program {

		private String channel;

		public RGBChannel(String colorchannel){
			this.channel = colorchannel;
		}

		@Override
		public String id(){
			return "fvtm:rgb_channel";
		}

		@Override
		public void pre(ModelGroup list, ModelRenderData data){
			Renderer120.setColor(data.color.getColorChannel(channel));
		}

		@Override
		public void post(ModelGroup list, ModelRenderData data){
			Renderer120.resetColor();
		}

		@Override
		public Program parse(String[] args){
			return new RGBChannel(args[0]);
		}

	}

}
