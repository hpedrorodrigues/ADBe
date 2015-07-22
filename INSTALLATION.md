# Installation

## On Ubuntu

To install Scala on Ubuntu, you get it either using.

```bash
  sudo apt-get install scala
```

or if you want to get the latest stable Scala release, here are the steps:

**First, install Sun Java**

- [x] Enable Multiverse repository, see [here](https://help.ubuntu.com/community/Repositories/Ubuntu).
- [x] [Install](https://help.ubuntu.com/community/Java) the Sun Java packages, choose it to be the default.

**Second, install Scala**

Download the latest Scala package, on scala-lang.org [here](http://www.scala-lang.org/download/).

```bash
  wget http://downloads.typesafe.com/scala/2.11.7/scala-2.11.7.tgz
```

Uncompress it to `/opt/`.

```bash
  tar -C /opt/ -xvzf scala-2.11.7.tgz
```

Add Scala to your $PATH by adding this line to .bashrc or .zshrc inside your home directory.

```bash
  PATH="$PATH:/opt/scala-2.11.7/bin"
```

Restart Ubuntu, start a terminal window and type to see you have successfully installed it.

```bash
 scala -version
```
