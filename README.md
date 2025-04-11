# TooBee Optimization

This project is a public minecraft optimization mod for TooBee survival server,
including a variety of aspects where each one is designed for a specific occasion.

The mod is in a very early stage of development, and notice that it is a dedicated server only mod.

Support for the newest minecraft is updated if and only if TooBee server updates,
and yet older version of minecraft is not supported.

## About TooBee Server

TooBee is a Chinese original survival server belonging to HaoRenFu.

The version of server equals to this latest minecraft version of this mod.

- Server IP: `2ob.top`
- Website: https://toobee.top
- QQ group: 786296062
- [Discord](https://discord.gg/YcJVpVKe8q)

## Mod Dependencies

Keep following mods as newest as possible, they are required.

- Fabric Language Kotlin
- Lithium

### Compatibility

TooBee Optimization are tested to be compatible with the following optimization mods.

- [C2ME](https://github.com/RelativityMC/C2ME-fabric)
- [Carpet](https://github.com/gnembon/fabric-carpet)
- [Chlorophyll](https://github.com/CyanidinMC/Chlorophyll)
- [FerriteCore](https://github.com/malte0811/FerriteCore)
- [Krypton](https://github.com/astei/krypton)
- [Lithium](https://github.com/CaffeineMC/lithium) (Required)
- [ModernFix](https://github.com/embeddedt/ModernFix)
- [Moonrise](https://github.com/Tuinity/Moonrise)
- [Noisium](https://github.com/Steveplays28/noisium)
- [ScalableLux](https://github.com/RelativityMC/ScalableLux)
- [Sepals](https://github.com/cao-awa/Sepals) (Targeted compatibility measure has been made)
- [ServerCore](https://github.com/Wesley1808/ServerCore)
- [WorldThreader](https://github.com/2No2Name/worldthreader)
- [VMP](https://github.com/RelativityMC/VMP-fabric)

[Async](https://github.com/AxalotLDev/Async) is planned to be supported in the future. I have to ensure enough thread safety.

## What has been done

Most modifications are ensure to be thread safe.

### Mob Stacking Optimization

Cache some of the calculation results of vast amount of certain entities stacking in a single position.
I try to optimize the time complexity of such calculation from $O(n^2\log n)$ to $O(n)$,
which means the MSPT caused by 3000 wardens can be reduced from more than 700 to less than 15.
However, notice that this mod is server only, so it should be no use on client side. 

#### These mobs are recommended to
- be the same type;
- stack in the same dimension and block pos;
- stand on a solid block steadily;
- have limited mobility;
- with climbable block in the position of their feet so that they don't crush.

#### Affected Mob
- **Warden**: usually for pseudo-peace farm in server.
- **Piglin**: usually for bartering farm.

#### Principle and Attention
Cache the calculation results of such mobs every tick:
One mob experience a full calculation process and cache the result, while others stacking in the same position share it.
In this way, original behaviour of mobs stacking in one block are slightly affected,
but not so much and most people don't care about it, as hardly any player interact with these thousands of entities.

### More reasonable despawn condition

If mobs pickup items, they keep alive permanently unless killed.
However, those mobs are not counted into spawn cap, making their population grows endlessly and finally causes huge lags.
While a hostile mob hold the following common items without any components, it will still despawn:

arrow, bone, brown mushroom, cobbled deepslate, cobblestone, dirt, egg, grass block, gravel, gunpowder,
pointed dripstone, red mushroom, rotten flesh, spider eye, string, torch, wheat seeds.

## Plan

- [x] Compatibility with [Sepals](https://github.com/cao-awa/Sepals) Mod.
- [ ] Compatibility with [Async](https://github.com/AxalotLDev/Async) Mod.
- [ ] Make the enderman holding certain blocks despawn. *(It's a little bit different from other hostile mobs)*
- [ ] Stacking shulkers optimization.
- [ ] Stacking whithers optimization.
- [ ] Reduce the calculation of spawning condition checking when pseudo-peace farm is running.
- [ ] A high speed moving player loads fewer chunks, in oven shape more precisely.
- [ ] (Quite hard) Optimize iron man farmer with more than six thousand villagers.
