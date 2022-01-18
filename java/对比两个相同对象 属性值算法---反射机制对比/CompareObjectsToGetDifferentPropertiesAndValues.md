```
public static Map diffAnimal(Map map,animal dog, animal cat) throws Exception {
        //判断两个对象是否为null
        if (null == dog || null == cat) {
            return null;
        }

        Class<? extends animal> dogClass = dog.getClass();
        Class<? extends animal> catClass = cat.getClass();
        Field[] fields = dogClass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            //获取get属性方法名
            String getMethodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            //获取方法调用
            Method dogClassMethod = dogClass.getMethod(getMethodName);
            Method catClassMethod = catClass.getMethod(getMethodName);
            //调用方法
            Object invoke = dogClassMethod.invoke(dog);
            Object catInvoke = catClassMethod.invoke(cat);
            String b1 = dogClassMethod.invoke(dog).toString();
            String b2 = catClassMethod.invoke(cat).toString();
            //进行判断
            if (b1.equals(b2)) {
                map.put(field.getName(), b2);
            } else {
                map.put(field.getName(), "");
            }
        }
        return map;
    }
```

