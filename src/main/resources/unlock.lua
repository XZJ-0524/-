
-- 比较2个标识
if(redis.call('get', KEYS[1]) == ARGV[1]) then
    -- del key
    return redis.call('del',KEYS[1])
end
return 0
