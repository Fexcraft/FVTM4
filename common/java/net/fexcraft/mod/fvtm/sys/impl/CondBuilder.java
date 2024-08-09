package net.fexcraft.mod.fvtm.sys.impl;

import net.fexcraft.mod.fvtm.data.part.PartData;
import net.fexcraft.mod.fvtm.data.part.PartFunction;
import net.fexcraft.mod.fvtm.sys.condition.Condition;
import net.fexcraft.mod.fvtm.sys.condition.Conditional;

import java.util.function.Function;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class CondBuilder {

	public static Function<Condition, Conditional> run(){
		return (cond) -> {
			switch(cond.type){
				case "attribute":{
					switch(cond.mode){
						case BOOL_EQUAL:{
							boolean bool = Boolean.parseBoolean(cond.condi);
							return data -> {
								return data.vehicle.getAttributeBoolean(cond.target, false) == bool;
							};
						}
						case BOOL_NEQUAL:{
							boolean bool = Boolean.parseBoolean(cond.condi);
							return data -> {
								return data.vehicle.getAttributeBoolean(cond.target, false) != bool;
							};
						}
						case NUMB_EQUAL:{
							Float val = Float.parseFloat(cond.condi);
							return data -> {
								return data.vehicle.getAttributeFloat(cond.target, 0f) == val;
							};
						}
						case NUMB_NEQUAL:{
							Float val = Float.parseFloat(cond.condi);
							return data -> {
								return data.vehicle.getAttributeFloat(cond.target, 0f) != val;
							};
						}
						case EQUAL:{
							return data -> {
								return data.vehicle.getAttributeString(cond.target, "null").equals(cond.condi);
							};
						}
						case NEQUAL:{
							return data -> {
								return !data.vehicle.getAttributeString(cond.target, "null").equals(cond.condi);
							};
						}
						case LEQUAL:{
							float val = Float.parseFloat(cond.condi);
							return data -> {
								return data.vehicle.getAttributeFloat(cond.target, 0f) <= val;
							};
						}
						case GEQUAL:{
							float val = Float.parseFloat(cond.condi);
							return data -> {
								return data.vehicle.getAttributeFloat(cond.target, 0f) >= val;
							};
						}
						case LESS:{
							float val = Float.parseFloat(cond.condi);
							return data -> {
								return data.vehicle.getAttributeFloat(cond.target, 0f) < val;
							};
						}
						case GREATER:{
							float val = Float.parseFloat(cond.condi);
							return data -> {
								return data.vehicle.getAttributeFloat(cond.target, 0f) > val;
							};
						}
					}
					break;
				}
				case "part_function":
				case "part_func":
				case "partfunc":{
					return mrdata -> {
						PartData data = mrdata.part_category.equals(cond.target) ? mrdata.part : mrdata.vehicle.getPart(cond.target);
						PartFunction func = data == null ? null : data.getFunction(cond.targets[1]);
						return func == null ? false : func.onCondition(cond.targets, cond.mode, cond.condi);
					};
				}
				case "multiple":
				case "multi":
				case "group":{
					//TODO
					break;
				}
				default:{
					//TODO run forge event
					break;
				}
			}
			return data -> false;
		};
	}

}
