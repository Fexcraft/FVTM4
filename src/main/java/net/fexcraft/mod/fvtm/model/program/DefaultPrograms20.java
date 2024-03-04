package net.fexcraft.mod.fvtm.model.program;

import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.mod.fvtm.model.ModelGroup;
import net.fexcraft.mod.fvtm.model.ModelRenderData;
import net.fexcraft.mod.fvtm.model.Program;
import net.fexcraft.mod.fvtm.render.Renderer120;

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
