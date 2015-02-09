import hashlib

translate = {
    '\\': '\\\\',
    '\n': '\\n',
    '\r': '\\r',
    '\t': '\\t',
    '"': '\\"',
}

def escape(line):
    return ''.join(translate.get(i) or i for i in line)

def escape_lua_file(path, cname):
    sha1 = hashlib.sha1()
    with open(path, 'rt') as fp:
        print 'const char *%s =' % cname
        for line in fp:
            sha1.update(line)
            print '    "%s"' % escape(line)
        print ';'
    print 'const char *%s_sha1 = "%s";' % (cname, sha1.hexdigest())

print "/* Don't edit this file it was automatically generated */"
escape_lua_file('scripts/lock.lua', 'script_lua_lock')
escape_lua_file('scripts/unlock.lua', 'script_lua_unlock')
