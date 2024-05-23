--
-- Created by IntelliJ IDEA.
-- User: Cherise
-- Date: 2021/8/12
-- Time: 21:38
-- To change this template use File | Settings | File Templates.
--
-- 通过KEYS数组 获取传给脚本的参数 从1开始
local userId = KEYS[1];
local goodsId = KEYS[2];
-- 通过..进行字符串的拼接   对应 库存的key 以及 订单的key
local cntKey = goodsId .. '_count';
local orderKey = goodsId .. '_' .. userId;
local orderExists = redis.call("get", orderKey);
if (orderExists and tonumber(orderExists) == 1) then
--    代表已被秒杀成功过
    return 2;
end
local num = redis.call("get", cntKey);
if (num and tonumber(num) <= 0) then
--    代表已被秒杀一空
    return 0;
else
--    秒杀成功
    redis.call("decr", cntKey);
    redis.call("set", orderKey, 1);
end
return 1;