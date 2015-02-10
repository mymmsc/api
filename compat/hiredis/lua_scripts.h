/* Don't edit this file it was automatically generated */
const char *script_lua_lock =
    "-- KEYS = {key_lock, [key_data]}\r\n"
    "-- ARGS = {timestamp, expire_at}\r\n"
    "if redis.call('SETNX', KEYS[1], ARGV[2]) == 1 then\r\n"
    "   -- ok\r\n"
    "   return {1, KEYS[2] and redis.call('GET', KEYS[2]) or false}\r\n"
    "else\r\n"
    "   local timestamp = tonumber(ARGV[1])\r\n"
    "   local lock_expires_at = tonumber(redis.call('GET', KEYS[1]) or 0)\r\n"
    "\r\n"
    "   if lock_expires_at < timestamp then\r\n"
    "      redis.call('SET', KEYS[1], ARGV[2])\r\n"
    "      -- expired\r\n"
    "      return {2, KEYS[2] and redis.call('GET', KEYS[2]) or false}\r\n"
    "   end\r\n"
    "end\r\n"
    "-- locked\r\n"
    "return {0, false}\r\n"
;
const char *script_lua_lock_sha1 = "4abf12ef4ed83a4fe5eb57784907209f6590e6b9";
const char *script_lua_unlock =
    "-- KEYS = {key_lock, [key_data]}\r\n"
    "-- ARGS = {expire_at, [data]}\r\n"
    "if redis.call('GET', KEYS[1]) ~= ARGV[1] then\r\n"
    "   return 0\r\n"
    "end\r\n"
    "if KEYS[2] then\r\n"
    "   if ARGV[2] then\r\n"
    "      redis.call('SET', KEYS[2], ARGV[2])\r\n"
    "   else\r\n"
    "      redis.call('DEL', KEYS[2])\r\n"
    "   end\r\n"
    "end\r\n"
    "redis.call('DEL', KEYS[1])\r\n"
    "return 1\r\n"
;
const char *script_lua_unlock_sha1 = "12645b418846be4d7adb1647fa1cac4cb13d55c9";
