-- KEYS = {key_lock, [key_data]}
-- ARGS = {expire_at, [data]}
if redis.call('GET', KEYS[1]) ~= ARGV[1] then
   return 0
end
if KEYS[2] then
   if ARGV[2] then
      redis.call('SET', KEYS[2], ARGV[2])
   else
      redis.call('DEL', KEYS[2])
   end
end
redis.call('DEL', KEYS[1])
return 1
