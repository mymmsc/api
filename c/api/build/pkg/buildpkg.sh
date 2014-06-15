#!/bin/sh
# buildpkg.sh: This script builds a Solaris PKG from the source tree
#              provided.

PREFIX=/usr/local
TEMPDIR=/var/tmp/$USER/api-root
rm -rf $TEMPDIR

api_src_dir=.

while test $# -gt 0 
do
  # Normalize
  case "$1" in
  -*=*) optarg=`echo "$1" | sed 's/[-_a-zA-Z0-9]*=//'` ;;
  *) optarg= ;;
  esac

  case "$1" in
  --with-api=*)
  api_src_dir=$optarg
  ;;
  esac

  shift
done

if [ -f "$api_src_dir/configure.in" ]; then
  cd $api_src_dir
else
  echo "The api source could not be found within $api_src_dir"
  echo "Usage: buildpkg [--with-api=dir]"
  exit 1
fi

./configure --prefix=$PREFIX
make
make install DESTDIR=$TEMPDIR
rm $TEMPDIR$PREFIX/lib/api.exp
. build/pkg/pkginfo
cp build/pkg/pkginfo $TEMPDIR$PREFIX

current=`pwd`
cd $TEMPDIR$PREFIX
echo "i pkginfo=./pkginfo" > prototype
find . -print | grep -v ./prototype | grep -v ./pkginfo | pkgproto | awk '{print $1" "$2" "$3" "$4" root bin"}' >> prototype
mkdir $TEMPDIR/pkg
pkgmk -r $TEMPDIR$PREFIX -d $TEMPDIR/pkg

cd $current
pkgtrans -s $TEMPDIR/pkg $current/$NAME-$VERSION-$ARCH-local
gzip $current/$NAME-$VERSION-$ARCH-local

rm -rf $TEMPDIR

