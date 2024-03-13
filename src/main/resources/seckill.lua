local voucherId = ARGV[1]
local userId = ARGV[2]

local stockKey = 'seckill:stock:' .. voucherId
local orderKey = 'seckill:order' .. voucherId

--判断秒杀资格
if (tonumber(redis.call('get',stockKey)) <= 0) then
    --库存不足
    return 1
end

if(redis.call('sismember', orderKey , userId)==1) then
    return 2
end

-- 扣库存
redis.call('incrby', stockKey, -1)
-- 下单(保存用户)
redis.call('sadd', orderKey, userId)

return 0