-- KEYS = {key_lock, [key_data]}
-- ARGS = {timestamp, expire_at}
if redis.call('SETNX', KEYS[1], ARGV[2]) == 1 then
   -- ok
   return {1, KEYS[2] and redis.call('GET', KEYS[2]) or false}
else
   local timestamp = tonumber(ARGV[1])
   local lock_expires_at = tonumber(redis.call('GET', KEYS[1]) or 0)

   if lock_expires_at < timestamp then
      redis.call('SET', KEYS[1], ARGV[2])
      -- expired
      return {2, KEYS[2] and redis.call('GET', KEYS[2]) or false}
   end
end
-- locked
return {0, false}
