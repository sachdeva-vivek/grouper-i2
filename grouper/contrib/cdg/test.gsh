#
# GrouperShell Tests
# $Id: test.gsh,v 1.3 2006-06-22 19:51:03 blair Exp $
#
version   = "0.0.1"
rootExtn  = "uchicago"
nsExtn    = "nsit"
gExtn     = "nbs"
subjType  = "person"
subjIdALL = "GrouperAll"
subjIdGS  = "GrouperSystem"
subjIdA   = "subj.A"
subjNameA = "subject a"
#
assert( "gsh version " + version    , version().equals(version)               )
assert( "reset registry"            , resetRegistry()                         )
assert( "3 sources"                 , getSources().size() == 3                )
subjGS    = findSubject(subjIdGS)
assert( "found GrouperSystem"       , subjGS.getId().equals(subjIdGS)         )
subjALL   = findSubject(subjIdALL)
assert( "found GrouperAll"          , subjGS.getId().equals(subjIdALL)        )
subjA     = addSubject(subjIdA, subjType, subjNameA)      
subj      = findSubject(subjIdA)
assert( "find subject: i"           , subj.getId().equals(subjA.getId())      )
subj      = findSubject(subjIdA, subjType)
assert( "find subject: i, t"        , subj.getId().equals(subjA.getId())      )
subj      = findSubject(subjIdA, subjType, subjNameA)
assert( "find subject: i, t, n"     , subj.getId().equals(subjA.getId())      )
assert( "0 stems after reset"       , getStems("").size() == 0                )
assert( "0 groups after reset"      , getGroups("").size() == 0               )
root      = addRootStem(rootExtn, rootExtn)
rootName  = root.getName()
assert( "added root stem"           , root instanceof Stem )
assert( "there is now 1 stem"       , getStems("").size() == 1                )
ns        = addStem(rootName, nsExtn, nsExtn)
nsName    = ns.getName()
assert( "added child stem"          , ns instanceof Stem                      )
assert( "there are now 2 stems"     , getStems("").size() == 2                )
g         = addGroup(nsName, nsExtn, gExtn)
gName     = g.getName()
assert( "added child group"         , g instanceof Group                      )
assert( "there is now 1 group"      , getGroups("").size() == 1               )
assert( "group has 0 members"       , getMembers(gName).size() == 0           )
assert( "added member to group"     , addMember(gName, subjIdA)               )
assert( "group now has 1 member"    , getMembers(gName).size() == 1           )
assert( "group hasMember"           , hasMember(gName, subjIdA)               )
assert( "deleted member from group" , delMember(gName, subjIdA)               )
assert( "group now has 0 members"   , getMembers(gName).size() == 0           )
assert( "deleted child group"       , delGroup(gName)                         )
assert( "there are now 0 groups"    , getGroups("").size() == 0               )
assert( "deleted child stem"        , delStem(nsName)                         )
assert( "there is now 1 stem"       , getStems("").size() == 1                )
assert( "deleted root stem"         , delStem(rootName)                       )
assert( "there are now 0 stems"     , getStems("").size() == 0                )
assert( "history()"                 , history()                               )
assert( "history(1)"                , history(1)                              )
assert( "last()"                    , last()                                  )
assert( "last(0)"                   , last(0)                                 )
quit
