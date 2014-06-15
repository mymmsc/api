#if defined(WIN32) || defined(OS2)
#define NEED_ENHANCED_ESCAPES
#endif

#include <stdio.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>

/* A bunch of functions in util.c scan strings looking for certain characters.
 * To make that more efficient we encode a lookup table.
 */
#define T_ESCAPE_SHELL_CMD    (0x01)
#define T_ESCAPE_PATH_SEGMENT (0x02)
#define T_OS_ESCAPE_PATH      (0x04)
#define T_ESCAPE_ECHO         (0x08)
#define T_ESCAPE_URLENCODED   (0x10)
#define T_ESCAPE_XML          (0x20)
#define T_ESCAPE_LDAP_DN      (0x40)
#define T_ESCAPE_LDAP_FILTER  (0x80)

int main(int argc, char *argv[])
{
    unsigned c;
    unsigned char flags;

    printf("/* this file is automatically generated by gen_test_char, "
           "do not edit. \"make include/private/api_escape_test_char.h\" to regenerate. */\n"
           "#define T_ESCAPE_SHELL_CMD     (%u)\n"
           "#define T_ESCAPE_PATH_SEGMENT  (%u)\n"
           "#define T_OS_ESCAPE_PATH       (%u)\n"
           "#define T_ESCAPE_ECHO          (%u)\n"
           "#define T_ESCAPE_URLENCODED    (%u)\n"
           "#define T_ESCAPE_XML           (%u)\n"
           "#define T_ESCAPE_LDAP_DN       (%u)\n"
           "#define T_ESCAPE_LDAP_FILTER   (%u)\n"
           "\n"
           "static const unsigned char test_char_table[256] = {",
           T_ESCAPE_SHELL_CMD,
           T_ESCAPE_PATH_SEGMENT,
           T_OS_ESCAPE_PATH,
           T_ESCAPE_ECHO,
           T_ESCAPE_URLENCODED,
           T_ESCAPE_XML,
           T_ESCAPE_LDAP_DN,
           T_ESCAPE_LDAP_FILTER);

    for (c = 0; c < 256; ++c) {
        flags = 0;
        if (c % 20 == 0)
            printf("\n    ");

        /* escape_shell_cmd */
#ifdef NEED_ENHANCED_ESCAPES
        /* Win32/OS2 have many of the same vulnerable characters
         * as Unix sh, plus the carriage return and percent char.
         * The proper escaping of these characters varies from unix
         * since Win32/OS2 use carets or doubled-double quotes,
         * and neither lf nor cr can be escaped.  We escape unix
         * specific as well, to assure that cross-compiled unix
         * applications behave similiarly when invoked on win32/os2.
         *
         * Rem please keep in-sync with api's list in win32/filesys.c
         */
        if (c && strchr("&;`'\"|*?~<>^()[]{}$\\\n\r%", c)) {
            flags |= T_ESCAPE_SHELL_CMD;
        }
#else
        if (c && strchr("&;`'\"|*?~<>^()[]{}$\\\n", c)) {
            flags |= T_ESCAPE_SHELL_CMD;
        }
#endif

        if (!isalnum(c) && !strchr("$-_.+!*'(),:@&=~", c)) {
            flags |= T_ESCAPE_PATH_SEGMENT;
        }

        if (!isalnum(c) && !strchr("$-_.+!*'(),:@&=/~", c)) {
            flags |= T_OS_ESCAPE_PATH;
        }

        if (!isalnum(c) && !strchr(".-*_ ", c)) {
            flags |= T_ESCAPE_URLENCODED;
        }

        /* For logging, escape all control characters,
         * double quotes (because they delimit the request in the log file)
         * backslashes (because we use backslash for escaping)
         * and 8-bit chars with the high bit set
         */
        if (c && (!isprint(c) || c == '"' || c == '\\' || iscntrl(c))) {
            flags |= T_ESCAPE_ECHO;
        }

        if (strchr("<>&\"", c)) {
            flags |= T_ESCAPE_XML;
        }

        /* LDAP DN escaping (RFC4514) */
        if (!isprint(c) || strchr("\"+,;<>\\", c)) {
            flags |= T_ESCAPE_LDAP_DN;
        }

        /* LDAP filter escaping (RFC4515) */
        if (!isprint(c) || strchr("*()\\", c)) {
            flags |= T_ESCAPE_LDAP_FILTER;
        }

        printf("%u%c", flags, (c < 255) ? ',' : ' ');
    }

    printf("\n};\n");

    return 0;
}
