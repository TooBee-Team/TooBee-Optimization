# TooBee Optimization

This project is a public minecraft optimization mod for TooBee survival server.

The mod is in a very early stage of development, and notice that it is a dedicated server only mod.

Support for the newest minecraft is updated if and only if TooBee server updates,
and yet older version of minecraft is not supported.

## About TooBee Server
TooBee is a Chinese original survival server belonging to HaoRenFu.

The version of server equals to this latest minecraft version of this mod.

- Server IP: `2ob.top:59522`
- Website (Temporarily not open): www.2ob.top
- QQ group: 786296062
- [Discord](https://discord.gg/r4eztFe5)

## Mod Dependencies
Keep following mods as newest as possible.

- Fabric API
- Fabric Language Kotlin
- Lithium

## What has been done
Most modifications are ensure to be thread safe.

Cache some of the calculation results of vast amount of certain entities stacking in a single position.
I try to optimize the time complexity of such calculation from $O(n^2\log n)$ to $O(n)$,
which means the MSPT caused by 3000 wardens can be reduced from more than 700 to less than 15.
However, notice that this mod is server only, so it should be no use on client side. 

More precisely, these mobs are recommended to:
- be the same type;
- stack in the same dimension and block pos;
- stand on a solid block steadily;
- have limited mobility;
- with climbable block in the position of their feet so that they don't crush.

### Affected Mob
- **Warden**: usually for pseudo-peace farm in server.