package com.garganttua.events.spec.objects.context;

import java.util.List;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.objects.GGEventsUtils;

public class GGEventsContextItemBinderUtils {
	
	public static <ContextItem, BoundItem extends IGGEventsContextItemBinder<ContextItem>> void bindList(List<BoundItem> fromBoundItemList, List<ContextItem> toContextItemList) throws GGEventsException {
		for(BoundItem item: fromBoundItemList){
			toContextItemList.add(item.bind());
		};
	}
	
	public static <ContextItem, BoundItem extends IGGEventsContextItemBinder<ContextItem>> void buildList(List<ContextItem> fromContextItemList, List<BoundItem> toBoundItemList, Class<BoundItem> boundItemClass) throws GGEventsException {
		for(ContextItem contextItem: fromContextItemList) {
			BoundItem item = null;
			item = GGEventsUtils.getInstanceOf(boundItemClass);
			item.build(contextItem);
			toBoundItemList.add(item);
		};
	}

}
