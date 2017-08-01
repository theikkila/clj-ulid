# Universally Unique Lexicographically Sortable Identifier

A Clojure port of [alizain/ulid](https://github.com/alizain/ulid)

[![Clojars Project](https://img.shields.io/clojars/v/clj-ulid.svg)](https://clojars.org/clj-ulid)
[![CircleCI](https://circleci.com/gh/theikkila/clj-ulid.svg?style=svg)](https://circleci.com/gh/theikkila/clj-ulid)

## Background

A GUID/UUID can be suboptimal for many use-cases because:

- It isn't the most character efficient way of encoding 128 bits
- It provides no other information than randomness

A ULID however:

- Is compatible with UUID/GUID's
- 1.21e+24 unique ULIDs per millisecond (1,208,925,819,614,629,174,706,176 to be exact)
- Lexicographically sortable
- Canonically encoded as a 26 character string, as opposed to the 36 character UUID
- Uses Crockford's base32 for better efficiency and readability (5 bits per character)
- Case insensitive
- No special characters (URL safe)


## Usage

Generating a ULID String requires a ULID instance.

```clojure

(require '[clj-ulid :as ulid])


; Generate ULID
(ulid/ulid)
; => "01bpf8bvjp4qas6vkg9maa13b5"

; Generate ULID from timestamp (useful for migrations)
(ulid/ulid 1501602817887)
; => "01bpf819tzbqdgxryyf664y7vs"

; Get embedded timestamp from ULID
(ulid/ulid->timestamp "01bpf819tzbqdgxryyf664y7vs")
; => 1501602817887

```

## Specification

Below is the current specification of ULID as implemented in this repository.

### Components

**Timestamp**
- 48 bits
- UNIX-time in milliseconds
- Won't run out of space till the year 10889 AD

**Entropy**
- 80 bits
- User defined entropy source.

### Encoding

[Crockford's Base32](http://www.crockford.com/wrmg/base32.html) is used as shown.
This alphabet excludes the letters I, L, O, and U to avoid confusion and abuse.

```
0123456789ABCDEFGHJKMNPQRSTVWXYZ
```


### String Representation

```
 01AN4Z07BY      79KA1307SR9X4MV3
|----------|    |----------------|
 Timestamp           Entropy
  10 chars           16 chars
   48bits             80bits
   base32             base32
```


## Test
```shell
lein test
```


## Prior Art

- [alizain/ulid](https://github.com/alizain/ulid)
